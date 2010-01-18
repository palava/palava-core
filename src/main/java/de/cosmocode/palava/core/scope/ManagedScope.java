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

package de.cosmocode.palava.core.scope;

import com.google.inject.Key;
import com.google.inject.Scope;

/**
 * The {@link ManagedScope} interface is built
 * on top of {@link Scope} and adds some more methods
 * that can be used to enter or exit the scope
 * manually.
 *
 * @author Willi Schoenborn
 */
public interface ManagedScope extends Scope {

    /**
     * Enters the scope.
     */
    void enter();
    
    /**
     * Seeds an instance initially using a key.
     * 
     * @param <T> the key type parameter
     * @param key the key
     * @param value the value
     */
    <T> void seed(Key<T> key, T value);
    
    /**
     * Seeds an instance initially using a class.
     * 
     * @param <T> the generic class type
     * @param type the type
     * @param value the value
     */
    <T> void seed(Class<T> type, T value);
    
    /**
     * Exits the scope.
     */
    void exit();
    
}
