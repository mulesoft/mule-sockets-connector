/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.socket.api.source;

import static java.lang.String.format;
import static java.lang.Thread.currentThread;
import static org.mule.extension.socket.internal.SocketUtils.WORK;
import static org.mule.runtime.core.api.util.ExceptionUtils.extractConnectionException;
import org.mule.extension.socket.api.SocketAttributes;
import org.mule.extension.socket.api.config.ListenerConfig;
import org.mule.extension.socket.api.connection.ListenerConnection;
import org.mule.extension.socket.api.worker.SocketWorker;
import org.mule.runtime.api.component.location.ComponentLocation;
import org.mule.runtime.api.connection.ConnectionException;
import org.mule.runtime.api.connection.ConnectionProvider;
import org.mule.runtime.api.exception.MuleException;
import org.mule.runtime.api.message.Error;
import org.mule.runtime.api.scheduler.Scheduler;
import org.mule.runtime.core.api.MuleContext;
import org.mule.runtime.core.api.scheduler.SchedulerService;
import org.mule.runtime.extension.api.annotation.dsl.xml.ParameterDsl;
import org.mule.runtime.extension.api.annotation.execution.OnError;
import org.mule.runtime.extension.api.annotation.execution.OnSuccess;
import org.mule.runtime.extension.api.annotation.param.Config;
import org.mule.runtime.extension.api.annotation.param.Connection;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.source.EmitsResponse;
import org.mule.runtime.extension.api.runtime.source.Source;
import org.mule.runtime.extension.api.runtime.source.SourceCallback;
import org.mule.runtime.extension.api.runtime.source.SourceCallbackContext;

import java.io.InputStream;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Listens for socket connections of the given protocol in the configured host and port.
 * <p>
 * Whenever a new connection is received, this {@link Source} will schedule a a {@link SocketWorker} that will handle the
 * communication for that particular connection.
 *
 * @since 1.0
 */
@EmitsResponse
public final class SocketListener extends Source<InputStream, SocketAttributes> {

  private static final Logger LOGGER = LoggerFactory.getLogger(SocketListener.class);

  @Inject
  private MuleContext muleContext;

  @Inject
  private SchedulerService schedulerService;

  @Connection
  private ConnectionProvider<ListenerConnection> connectionProvider;

  private ListenerConnection connection;

  @Config
  private ListenerConfig config;

  private ComponentLocation location;

  private AtomicBoolean stopRequested = new AtomicBoolean(false);
  private Scheduler workManager;
  private Scheduler listenerExecutor;

  private Future<?> submittedListenerTask;

  /**
   * {@inheritDoc}
   */
  @Override
  public void onStart(SourceCallback<InputStream, SocketAttributes> sourceCallback) throws MuleException {
    connection = connectionProvider.connect();
    workManager = schedulerService
        .ioScheduler(muleContext.getSchedulerBaseConfig().withName(format("%s.socket.worker", location.getRootContainerName())));

    stopRequested.set(false);

    listenerExecutor = schedulerService.customScheduler(muleContext.getSchedulerBaseConfig().withMaxConcurrentTasks(1)
        .withName(format("%s.socket.listener", location.getRootContainerName())));
    submittedListenerTask = listenerExecutor.submit(() -> listen(sourceCallback));
  }

  @OnSuccess
  public void onSuccess(@Optional(defaultValue = "#[payload]") @ParameterDsl(allowReferences = false) InputStream responseValue,
                        SourceCallbackContext context) {
    context.<SocketWorker>getVariable(WORK)
        .ifPresent(worker -> worker.onComplete(responseValue));
  }


  @OnError
  public void onError(Error error, SourceCallbackContext context) {
    context.<SocketWorker>getVariable(WORK)
        .ifPresent(woker -> woker.onError(error.getCause()));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void onStop() {
    stopRequested.set(true);
    if (submittedListenerTask != null) {
      submittedListenerTask.cancel(false);
    }

    if (listenerExecutor != null) {
      listenerExecutor.stop();
    }

    if (workManager != null) {
      workManager.stop();
    }

    if (connection != null) {
      connectionProvider.disconnect(connection);
    }
  }

  private boolean isRequestedToStop() {
    return stopRequested.get() || currentThread().isInterrupted();
  }

  private void listen(SourceCallback<InputStream, SocketAttributes> sourceCallback) {
    for (;;) {
      if (isRequestedToStop()) {
        return;
      }

      try {
        SocketWorker worker = connection.listen(sourceCallback);
        worker.onError(e -> {
          if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(format("Got exception '%s'. Work being executed was: %s", e.getClass().getName(), worker.toString()));
          }

          extractConnectionException(e).ifPresent(sourceCallback::onConnectionException);
        });
        workManager.execute(worker);
      } catch (ConnectionException e) {
        if (!isRequestedToStop()) {
          sourceCallback.onConnectionException(e);
        }
      } catch (Exception e) {
        if (isRequestedToStop()) {
          return;
        }

        if (LOGGER.isDebugEnabled()) {
          LOGGER.debug("An exception occurred while listening for new connections", e);
        }
      }
    }
  }
}
