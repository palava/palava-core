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

import java.io.IOException;

import de.cosmocode.palava.core.framework.Service;

/**
 * A {@link Listener} is able to listen
 * on one specific port and establish a socket
 * connection. 
 *
 * @author Willi Schoenborn
 */
public interface Listener extends Service {

    /**
     * Starts this socket connector, which
     * opens a server socket.
     * 
     * @throws IOException if socket creation failed.
     */
    void run() throws IOException;
    
    /**
     * Blocks until socket has been closed.
     */
    void stop();
    
}
