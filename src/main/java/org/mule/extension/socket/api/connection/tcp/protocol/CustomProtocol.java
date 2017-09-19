/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.socket.api.connection.tcp.protocol;

import static java.lang.String.format;
import org.mule.extension.socket.api.socket.tcp.TcpProtocol;
import org.mule.runtime.api.util.LazyValue;
import org.mule.runtime.core.api.util.ClassUtils;
import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.dsl.xml.TypeDsl;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.annotation.param.display.Summary;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @since 1.0
 */
@TypeDsl(allowTopLevelDefinition = true)
public class CustomProtocol implements TcpProtocol {

  /**
   * Reference to full qualifier class name that should extend {@link TcpProtocol} that will be used as a custom protocol
   */
  @Parameter
  @Alias("class")
  @Summary("Full qualifier class name that must implement 'TcpProtocol' that will be used as a custom protocol")
  @DisplayName("Protocol Class Name")
  private String clazz;

  private LazyValue<TcpProtocol> delegate = new LazyValue<>(this::createProtocol);

  public CustomProtocol() {}

  public String getClazz() {
    return clazz;
  }

  @Override
  public InputStream read(InputStream is) throws IOException {
    return delegate.get().read(is);
  }

  @Override
  public void write(OutputStream os, InputStream data) throws IOException {
    delegate.get().write(os, data);
  }

  private TcpProtocol createProtocol() {
    try {
      return (TcpProtocol) ClassUtils.instantiateClass(clazz);
    } catch (Exception e) {
      throw new RuntimeException(format("Could not load class '%s'", clazz));
    }
  }
}
