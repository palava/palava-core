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
import java.util.Map;

import de.cosmocode.palava.core.scope.Destroyable;
import de.cosmocode.palava.core.session.HttpSession;

/**
 * 
 *
 * @author Willi Schoenborn
 */
public interface HttpRequest extends Destroyable {

    URI getRequestUri();
    
    String getRemoteAddress();
    
    String getUserAgent();
    
    HttpSession getHttpSession();
    
    <K, V> void set(K key, V value);
    
    <K> boolean contains(K key);
    
    <K, V> V get(K key);
    
    Map<Object, Object> getAttributes();
    
}
