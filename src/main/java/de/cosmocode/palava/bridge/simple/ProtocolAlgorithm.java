/**
 * palava - a java-php-bridge
 * Copyright (C) 2007-2010  CosmoCode GmbH
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

package de.cosmocode.palava.bridge.simple;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import de.cosmocode.palava.bridge.simple.content.Content;

public interface ProtocolAlgorithm {
    
    Map<String, String> open(Header header, InputStream input, OutputStream output);

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
    Header read(InputStream input);
    
    void sendTo(Content content, OutputStream output) throws IOException;
    
}
