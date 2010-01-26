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

package de.cosmocode.palava.core.request;

import java.net.URI;
import java.net.URL;

import de.cosmocode.palava.core.scope.Destroyable;
import de.cosmocode.palava.core.session.HttpSession;

/**
 * Encapsulates http related data access and scoping issues.
 *
 * @author Willi Schoenborn
 */
public interface HttpRequest extends Destroyable {

    /**
     * Provide the requested {@link URI}.
     * 
     * @return the requested uri
     * @throws IllegalStateException if there is no request uri information
     *         available in this request
     * @throws IllegalArgumentException if the request uri is no valid {@link URI}
     */
    URI getRequestUri();
    
    /**
     * Provide the requested referer.
     * 
     * @return the referer of this request or null if there
     *         was no information about the referer available
     * @throws IllegalArgumentException if the referer is no valid {@link URL}
     */
    URL getReferer();
    
    /**
     * Provide the remote address of the user.
     * 
     * @return the remote address
     * @throws IllegalStateException if there is no remote address
     *         information available in this request
     */
    String getRemoteAddress();
    
    /**
     * Provide the user agent.
     * 
     * @return the user agent of this request or null if there
     *         was no information about the user agent available
     */
    String getUserAgent();
    
    /**
     * Provide the associated session.
     * 
     * @return this request's session
     */
    HttpSession getHttpSession();
    
    /**
     * Sets an attribute in this request.
     * 
     * @param <K> the key type
     * @param <V> the value type
     * @param key the key
     * @param value the value
     */
    <K, V> void set(K key, V value);
    
    /**
     * Checks for the presence of a binding to the specified
     * key.
     * 
     * @param <K> the key type
     * @param key the key
     * @return true of this request contains a binding to the specified
     *         key
     */
    <K> boolean contains(K key);
    
    /**
     * Retrieves the value bound the specified key. 
     * 
     * @param <K> the key type
     * @param <V> the value type
     * @param key the key
     * @return the value associated to the specified key or null if
     *         there is no such binding
     */
    <K, V> V get(K key);
    
}
