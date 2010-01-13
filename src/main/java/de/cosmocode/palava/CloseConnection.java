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

/**
 * Not a real exception, thrown by the close-job.
 * Indicates, that the server should close the connection
 * without an error.
 * 
 * @author Tobias Sarnowski
 */
public final class CloseConnection extends Exception {
    
    private static final long serialVersionUID = 6642833065438659444L;
    
    private static final CloseConnection INSTANCE = new CloseConnection();
    
    private CloseConnection() {
        
    }
    
    public static CloseConnection getInstance() {
        return INSTANCE;
    }
    
}
