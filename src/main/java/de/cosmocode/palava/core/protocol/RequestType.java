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

import java.io.InputStream;

import de.cosmocode.palava.RequestHeader;

/**
 * Used to create different request types.
 *
 * @author Willi Schoenborn
 */
public enum RequestType {

    /**
     * Used for GET-like parameter passing.
     * 
     * @deprecated use {@link RequestType#JSON} instead.
     */
    @Deprecated
    DATA {
        
        @Override
        public Request createRequest(RequestHeader header, InputStream stream) {
            return new DataRequest(header, stream);
        }
        
    },
    
    TEXT {
        
        @Override
        public Request createRequest(RequestHeader header, InputStream stream) {
            return new TextRequest(header, stream);
        }
        
    },
    
    JSON {
        
        @Override
        public Request createRequest(RequestHeader header, InputStream stream) {
            return new JSONRequest(header, stream);
        }
        
    },
    
    BINARY {
        
        @Override
        public Request createRequest(RequestHeader header, InputStream stream) {
            return new BinaryRequest(header, stream);
        }
        
    };
    
    /**
     * Creates a new {@link Request} based on this value.
     * 
     * @param header the request header
     * @param stream the stream to read from
     * @return a new {@link Request}
     */
    public abstract Request createRequest(RequestHeader header, InputStream stream);
    
}
