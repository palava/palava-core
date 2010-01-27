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

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;

import de.cosmocode.palava.bridge.command.Command;
import de.cosmocode.palava.bridge.request.HttpRequest;

/**
 * parse the request content as one big plain text.
 * 
 * @author Tobias Sarnowski
 */
public class SimpleTextCall extends AbstractCall {
    
    private String text;

    SimpleTextCall(HttpRequest request, Command command, Header header, InputStream stream) {
        super(request, command, header, stream);
    }

    public String getText() throws ConnectionLostException {
        if (text == null) {
            final byte[] buffer = new byte[getHeader().getContentLength()];
            read(buffer);
            final ByteBuffer bb = ByteBuffer.wrap(buffer);
            final CharBuffer cb = CHARSET.decode(bb);
            text = cb.toString();
        }
        return text;
    }

}
