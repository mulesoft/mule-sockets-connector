/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.socket.api.connection.tcp.protocol;

import static org.mule.extension.socket.internal.SocketUtils.logIfDebugEnabled;
import static org.mule.runtime.core.api.util.IOUtils.copyLarge;
import org.mule.runtime.extension.api.annotation.dsl.xml.TypeDsl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * <p>
 * The XmlMessageProtocol is an application level tcp protocol that can be used to read streaming xml documents. The only
 * requirement is that each document include an xml declaration at the beginning of the document of the form "<?xml...". In
 * section 2.8, the xml 1.0 standard contains "Definition: XML documents <strong>SHOULD</strong> begin with an XML declaration
 * which specifies the version of XML being used" while the xml 1.1 standard contains "Definition: XML 1.1 documents
 * <strong>MUST</strong> begin with an XML declaration which specifies the version of XML being used". The SHOULD indicates a
 * recommendation that, if not followed, needs to be carefully checked for unintended consequences. MUST indicates a mandatory
 * requirement for a well-formed document. Please make sure that the xml documents being streamed begin with an xml declaration
 * when using this class.
 * </p>
 * <p>
 * Data is read until a new document is found or there is no more data (momentarily). For slower networks,
 * {@link XmlMessageEOFProtocol} may be more reliable.
 * </p>
 * <p>
 * Also, because the default character encoding for Anypoint Platform is used to decode the message bytes when looking for the
 * XML declaration, some caution with message character encodings is warranted.
 * </p>
 * <p>
 * Finally, this class uses a PushbackInputStream to enable parsing of individual messages. The stream stores any pushed-back
 * bytes into it's own internal buffer and not the original stream. Therefore, the read buffer size is intentionally limited to
 * insure that unread characters remain on the stream so that all data may be read later.
 * </p>
 */
@TypeDsl(allowTopLevelDefinition = true)
public class XmlMessageProtocol extends AbstractByteProtocol {

  private static final String XML_PATTERN = "<?xml";

  private static final int READ_BUFFER_SIZE = 4096;
  private static final int PUSHBACK_BUFFER_SIZE = READ_BUFFER_SIZE * 2;
  private static final Logger LOGGER = LoggerFactory.getLogger(XmlMessageProtocol.class);

  private ConcurrentMap pbMap = new ConcurrentHashMap();

  public XmlMessageProtocol() {
    super(STREAM_OK);
  }

  @Override
  public InputStream read(InputStream socketIs) throws IOException {
    return nullIfEmptyArray(consume(socketIs));
  }

  private byte[] consume(InputStream is) throws IOException {
    PushbackInputStream pbis = (PushbackInputStream) pbMap.get(is);
    if (null == pbis) {
      pbis = new PushbackInputStream(is, PUSHBACK_BUFFER_SIZE);
      PushbackInputStream prev = (PushbackInputStream) pbMap.putIfAbsent(is, pbis);
      pbis = null == prev ? pbis : prev;
    }

    int len = -1;
    try {
      // read until xml pattern is seen (and then pushed back) or no more data
      // to read. return all data as message
      byte[] buffer = new byte[READ_BUFFER_SIZE];
      StringBuilder message = new StringBuilder(READ_BUFFER_SIZE);
      int patternIndex = -1;
      boolean repeat;
      do {
        len = safeRead(pbis, buffer);
        if (len >= 0) {
          // TODO support encoding MULE-9900
          message.append(new String(buffer, 0, len));
          // start search at 2nd character in buffer (index=1) to
          // indicate whether we have reached a new document.
          patternIndex = message.toString().indexOf(XML_PATTERN, 1);
          repeat = isRepeat(patternIndex, len, pbis.available());
        } else {
          // never repeat on closed stream (and avoid calling available)
          repeat = false;
        }

      } while (repeat);

      if (patternIndex > 0) {
        // push back the start of the next message and
        // ignore the pushed-back characters in the return buffer
        pbis.unread(message.substring(patternIndex, message.length()).getBytes());
        message.setLength(patternIndex);
      }

      logIfDebugEnabled(message.toString(), LOGGER);

      return message.toString().getBytes();
    } finally {
      // TODO - this doesn't seem very reliable, since loop above can end
      // without EOF. On the other hand, what else can we do? Entire logic
      // is not very dependable, IMHO. XmlMessageEOFProtocol is more likely
      // to be correct here, I think.

      // clear from map if stream has ended
      if (len < 0) {
        pbMap.remove(is);
      }
    }
  }

  @Override
  public void write(OutputStream outputStream, InputStream data) throws IOException {
    copyLarge(data, outputStream);
  }

  /**
   * Show we continue reading? This class, following previous implementations, only reads while input is saturated.
   *
   * @param patternIndex The index of the xml tag (or -1 if the next message not found)
   * @param len The amount of data read this loop (or -1 if EOF)
   * @param available The amount of data available to read
   * @return true if the read should continue
   * @see XmlMessageEOFProtocol
   */
  protected boolean isRepeat(int patternIndex, int len, int available) {
    return patternIndex < 0 && len == READ_BUFFER_SIZE && available > 0;
  }
}
