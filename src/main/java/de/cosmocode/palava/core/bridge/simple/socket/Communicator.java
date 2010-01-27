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

package de.cosmocode.palava.core.bridge.simple.socket;

import java.io.InputStream;
import java.io.OutputStream;

import de.cosmocode.palava.core.framework.Service;

/**
 * A communicator handles exactly one incoming http request,
 * which itself results in one socket connections.
 *
 * @author Willi Schoenborn
 */
public interface Communicator extends Service {

    /**
     * Communicate with the php frontend using the given input and
     * output streams. A communication usually consists of multiple
     * call/response cycles.
     * 
     * @param input the socket input stream
     * @param output the socket output stream
     */
    void communicate(InputStream input, OutputStream output);
    
}
