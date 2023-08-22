/*
 * Copyright © MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.socket.api.connection.tcp.protocol;

import org.mule.extension.socket.api.socket.tcp.TcpProtocol;
import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.dsl.xml.TypeDsl;

/**
 * This protocol is an application level {@link TcpProtocol} that does nothing. Reading is terminated by the stream being closed
 * by the client.
 * When clients connect to a server using the EOF protocol, they write the message on the socket connection output stream.
 * To signal the end of the message, clients must signal the end of file (EOF) by closing the output stream and keeping the input stream open,
 * waiting for a response from the server. This behavior is known as half-close. Then, the server sends a response and closes its output stream and the whole socket connection.
 *
 * For TLS secured connections, use TLS 1.3 and JDK 11 as the half-close feature is not supported in TLS 1.2 and earlier versions.
 * The half-close feature triggers the close notification alert to prevent TLS truncation attacks. JDK 11 and newer versions support TLS 1.3.
 * Also, pooling profile is not compatible with `EofProtocol`.
 * <p>
 *
 * @since 1.0
 */
@Alias("eof-protocol")
@TypeDsl(allowTopLevelDefinition = true)
public class EOFProtocol extends DirectProtocol {

  /**
   * Repeat until EOF
   *
   * @param len Amount transferred last call (-1 on EOF or socket error)
   * @param available Amount available
   * @return true if the transfer should continue
   */
  @Override
  protected boolean isRepeat(int len, int available) {
    return true;
  }

}
