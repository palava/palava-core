/**
 * palava - a java-php-bridge
 * Copyright (C) 2007  CosmoCode GmbH
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package de.cosmocode.palava.core.protocol;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

import de.cosmocode.palava.core.protocol.content.Content;

/**
 * Default protocol algorithm.
 * 
 * @author Oliver Lorenz
 * @author Willi Schoenborn
 */
final class DefaultProtocolAlgorithm implements ProtocolAlgorithm {
    
    private static final Logger log = LoggerFactory.getLogger(DefaultProtocolAlgorithm.class);

    /**
     * Identifies the different parts of the protocol structure.
     *
     * @author Willi Schoenborn
     */
    private static enum Part {
     
        TYPE {
          
            @Override
            public Part writeTo(char c, StringBuilder target) {
                if (c == ':') {
                    return Part.COLON;
                } else {
                    target.append(c);
                }
                return this;
            }
            
        },
        
        COLON {
            
            @Override
            public Part writeTo(char c, StringBuilder target) {
                if (c == '/') {
                    return Part.FIRST_DOUBLE_SLASH;
                } else {
                    throw new ProtocolException('/', c);
                }
            }
            
        },
        
        FIRST_DOUBLE_SLASH {
            
            @Override
            public Part writeTo(char c, StringBuilder target) {
                if (c == '/') {
                    return Part.NAME;
                } else {
                    throw new ProtocolException('/', c);
                }
            }
            
        },
        
        NAME {
            
            @Override
            public Part writeTo(char c, StringBuilder target) {
                if (c == '/') {
                    return Part.SESSION_ID;
                } else {
                    target.append(c);
                }
                return this;
            }
            
        },
        
        SESSION_ID {
            
            @Override
            public Part writeTo(char c, StringBuilder target) {
                if (c == '/') {
                    return Part.LEFT_PARENTHESIS;
                } else {
                    target.append(c);
                }
                return this;
            }
            
        },
        
        LEFT_PARENTHESIS {
            
            @Override
            public Part writeTo(char c, StringBuilder target) {
                if (c == '(') {
                    return Part.CONTENT_LENGTH;
                } else {
                    throw new ProtocolException('(', c);
                }
            }
            
        },
        
        CONTENT_LENGTH {

            @Override
            public Part writeTo(char c, StringBuilder target) {
                if (c == ')') {
                    return QUESTION_MARK;
                } else {
                    target.append(c);
                }
                return this;
            }
            
        },
        
        QUESTION_MARK {
         
            @Override
            public Part writeTo(char c, StringBuilder target) {
                return this;
            }
            
        };
        
        public abstract Part writeTo(char c, StringBuilder target);
        
    }

    /**
     * Parses the palava protocol:
     * 
     * <p>
     *   {@code <type>://<aliasedName>/<sessionId>/(<contentLength>)?<content>}
     * </p>
     * 
     * <p>
     *   After the header has been parsed, the next byte read from the stream
     *   is the first byte of the actual content.
     * </p>
     * 
     * @param input the input to read from
     * @return a parsed Header
     * @throws ProtocolException if the supplied stream does not contain a valid input
     * @throws ConnectionLostException if connection to the socket broke up during reading
     */
    public Header read(InputStream input) {
        final StringBuilder type = new StringBuilder(6);
        final StringBuilder aliasedName = new StringBuilder(50);
        final StringBuilder sessionId = new StringBuilder(64);
        final StringBuilder contentLength = new StringBuilder(6);
        
        Part part = Part.TYPE;
        
        while (true) {
            final char c = readFrom(input);
            
            switch (part) {
                case TYPE: {
                    part = part.writeTo(c, type);
                    break;
                }
                case COLON: {
                    part = part.writeTo(c, null);
                    break;
                }
                case FIRST_DOUBLE_SLASH: {
                    part = part.writeTo(c, null);
                    break;
                }
                case NAME: {
                    part = part.writeTo(c, aliasedName);
                    break;
                }
                case SESSION_ID: {
                    part = part.writeTo(c, sessionId);
                    break;
                }
                case LEFT_PARENTHESIS: {
                    part = part.writeTo(c, null);
                    break;
                }
                case CONTENT_LENGTH: {
                    part = part.writeTo(c, contentLength);
                    break;
                }
                default: {
                    if (c == '?') {
                        log.debug("Session ID in header: {}", sessionId);
                        return new DefaultHeader(
                            CallType.valueOf(type.toString().toUpperCase()),
                            aliasedName.toString(),
                            sessionId.toString(),
                            Integer.parseInt(contentLength.toString())
                        );
                    } else {
                        throw new ProtocolException('?', c);
                    }
                }
            }
            
        }
        
    }
    
    private char readFrom(InputStream input) {
        try {
            final char c = (char) input.read();
            if (c == -1) {
                throw new ConnectionLostException("Reached end of stream");
            }
            return c;
        } catch (IOException e) {
            throw new ConnectionLostException(e);
        }
    }

    @Override
    public void sendTo(Content content, OutputStream output) throws IOException {
        Preconditions.checkNotNull(content, "Content");
        Preconditions.checkNotNull(output, "Output");
        final BufferedOutputStream buffered = new BufferedOutputStream(output);

        // write header
        final String header = String.format("%s://(%s)?", content.getMimeType(), content.getLength());
        log.debug("Writing header: {}", header);
        buffered.write(header.getBytes());

        // write body
        log.debug("Writing content: {}", content);
        content.write(buffered);
        buffered.flush();
    }

}
