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

import de.cosmocode.palava.bridge.call.Call;
import de.cosmocode.palava.bridge.command.Command;
import de.cosmocode.palava.bridge.request.HttpRequest;

/**
 * Used to create different request types.
 *
 * @author Willi Schoenborn
 */
public enum CallType {

    /**
     * Used for GET-like parameter passing.
     * 
     * @deprecated use {@link RequestType#JSON} instead.
     */
    @Deprecated
    DATA {
        
        @Override
        public Call createCall(HttpRequest request, Command command, Header header, InputStream stream) {
            return new SimpleDataCall(request, command, header, stream);
        }
        
    },
    
    TEXT {
        
        @Override
        public Call createCall(HttpRequest request, Command command, Header header, InputStream stream) {
            return new SimpleTextCall(request, command, header, stream);
        }
        
    },
    
    JSON {

        @Override
        public Call createCall(HttpRequest request, Command command, Header header, InputStream stream) {
            return new SimpleJsonCall(request, command, header, stream);
        }
        
    },
    
    BINARY {

        @Override
        public Call createCall(HttpRequest request, Command command, Header header, InputStream stream) {
            return new SimpleBinaryCall(request, command, header, stream);
        }
        
    },
    
    OPEN {

        @Override
        public Call createCall(HttpRequest request, Command command, Header header, InputStream stream) {
            return new SimpleJsonCall(request, command, header, stream);
        }
        
    },
    
    CLOSE {
        
        @Override
        public Call createCall(HttpRequest request, Command command, Header header, InputStream stream) {
            throw new UnsupportedOperationException();
        }
        
    };
    
    /**
     * Creates a new {@link Call} based on this value.
     * 
     * @param request the request
     * @param command the command
     * @param header the header
     * @param stream the stream to read from
     * @return a new {@link Call}
     */
    public abstract Call createCall(HttpRequest request, Command command, Header header, InputStream stream);
    
}
