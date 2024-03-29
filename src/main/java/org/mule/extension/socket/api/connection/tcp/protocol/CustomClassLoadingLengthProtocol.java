/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.socket.api.connection.tcp.protocol;

import org.mule.runtime.extension.api.annotation.dsl.xml.TypeDsl;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import org.apache.commons.io.input.ClassLoaderObjectInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.mule.extension.socket.internal.SocketUtils.logIfDebugEnabled;

/**
 * A length protocol that uses a specific class loader to load objects from streams.
 *
 * @since 1.0
 */
@TypeDsl(allowTopLevelDefinition = true)
public class CustomClassLoadingLengthProtocol extends LengthProtocol {

  @Parameter
  @Optional
  private ClassLoader classLoader;
  private static final Logger LOGGER = LoggerFactory.getLogger(CustomClassLoadingLengthProtocol.class);

  @Override
  public InputStream read(InputStream is) throws IOException {
    return new ClassLoaderObjectInputStream(this.getClassLoader(), logIfDebugEnabled(is, LOGGER));
  }

  public ClassLoader getClassLoader() {
    if (this.classLoader == null) {
      this.classLoader = this.getClass().getClassLoader();
    }
    return classLoader;
  }

  public void setClassLoader(ClassLoader classLoader) {
    this.classLoader = classLoader;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    if (!super.equals(o))
      return false;
    CustomClassLoadingLengthProtocol that = (CustomClassLoadingLengthProtocol) o;
    return Objects.equals(classLoader, that.classLoader);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), classLoader);
  }
}
