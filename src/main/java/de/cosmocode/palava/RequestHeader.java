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

package de.cosmocode.palava;

import java.io.IOException;
import java.io.InputStream;

import de.cosmocode.palava.core.protocol.RequestType;

/**
 * parse an inputstream for a palava protocol header and represent it as an object.
 * 
 * @author Tobias Sarnowski
 */
public class RequestHeader {

    private final RequestType type; 
    private final String job;
    private final String sessionId;
    private final long length;

    public RequestHeader(String type, String job, String sessionId, long length) {
        this.type = RequestType.valueOf(type.toUpperCase());
        this.job = job;
        this.sessionId = sessionId;
        this.length = length;
    }

    public RequestType getType() {
        return type;
    }

    public String getJob() {
        return job;
    }

    public String getSessionID() {
        return sessionId;
    }

    public long getContentLength() {
        return length;
    }

    public static RequestHeader createHeader(InputStream in) throws ProtocolErrorException, ConnectionLostException {
        boolean headerComplete = false;
        int part = 0;

        final StringBuilder type = new StringBuilder();
        final StringBuilder job = new StringBuilder();
        final StringBuilder sessionId = new StringBuilder();
        final StringBuilder length = new StringBuilder();

        while (true) {
            if (headerComplete) break;
            
            char c = 0;
            
            try {
                c = (char) in.read();
            } catch (IOException e) {
                throw new ConnectionLostException(e);
            }
            
            if (c == -1) {
                throw new ConnectionLostException("Stream was empty");
            }

            // parse the byte
            switch (part) {
                case 0: {
                    // type
                    if (c == ':') {
                        part++;
                    } else {
                        type.append(c);
                    }
                    break;
                }
                case 1: {
                    //  /
                }
                case 2: {
                    if (c == '/') {
                        part++;
                    } else {
                        throw new ProtocolErrorException();
                    }
                    break;
                }
                case 3: {
                    //  job /
                    if (c == '/') {
                        part++;
                    } else {
                        job.append(c);
                    }
                    break;
                }
                case 4: {
                    //  session_id /
                    if (c == '/') {
                        part++;
                    } else {
                        sessionId.append(c);
                    }
                    break;
                }
                case 5: {
                    //  (
                    if (c == '(') {
                        part++;
                    } else {
                        throw new ProtocolErrorException();
                    }
                    break;
                }
                case 6: {
                    //  content-length )
                    if (c == ')') {
                        part++;
                    } else {
                        length.append(c);
                    }
                    break;
                }
                case 7: {
                    //  ?
                    if (c == '?') {
                        headerComplete = true;
                    } else {
                        throw new ProtocolErrorException();
                    }
                    break;
                }
                default: {
                    break;
                }
                        
            }
        }

        return new RequestHeader(
            type.toString(),
            job.toString(), 
            sessionId.toString(), 
            Long.parseLong(length.toString())
        );
    }

}
