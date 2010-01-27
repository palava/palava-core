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

package de.cosmocode.palava.core.bridge.call;

import java.io.IOException;
import java.io.InputStream;

import de.cosmocode.palava.core.bridge.command.Command;
import de.cosmocode.palava.core.bridge.request.HttpRequest;
import de.cosmocode.palava.core.bridge.simple.ConnectionLostException;
import de.cosmocode.palava.core.bridge.simple.Header;

/**
 * A Call represents one single access.
 *
 * @author Willi Schoenborn
 */
public interface Call {

    /**
     * Provide the arguments of this call.
     * 
     * @return the arguments
     * @throws UnsupportedOperationException if this call does not support
     *         Arguments
     */
    Arguments getArguments();
    
    /**
     * Provide the surrounding {@link HttpRequest}.
     * 
     * @return the request
     */
    HttpRequest getHttpRequest();
    
    /**
     * Provide the command associated with this call.
     * 
     * @return the command scheduled for this call
     */
    Command getCommand();

    /**
     * Provides the underlying inputstream.
     * 
     * @return the inputstream
     */
    InputStream getInputStream();
    
    /**
     * Provide the header, this call has been build upon.
     * 
     * @return the header
     */
    Header getHeader();
    
    /**
     * Discard all bytes left in the stream without parsing them.
     * 
     * @throws ConnectionLostException if stream has been closed
     * @throws IOException if an error occurs during read
     */
    void discard() throws ConnectionLostException, IOException;
    
}
