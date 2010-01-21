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

package de.cosmocode.palava.core.registry;

/**
 * A {@link Registry} is used to provide a publish/subscribe
 * mechanism.
 *
 * @author Willi Schoenborn
 */
public interface Registry {

    /**
     * Register a listener for a specific type.
     * 
     * @param <T> the generic type
     * @param type the type's class
     * @param listener the listener
     * @throws NullPointerException if type or listener is null
     */
    <T> void register(Class<T> type, T listener);
    
    /**
     * Provide all listeners for a specific type.
     * 
     * <p>
     *   Note: Implementations may provide a live view which
     *   supports write through operations.
     * </p>
     * 
     * @param <T> the generic type
     * @param type the type's class
     * @return an iterable over all found listeners for that type
     * @throws NullPointerException if type is null
     */
    <T> Iterable<T> getListeners(Class<T> type);
    
    /**
     * Notify all listeners for a specific type
     * by invoking command on every found listener.
     * 
     * @param <T> the generic type
     * @param type the type's class
     * @param command the command being invoked on every listener
     * @throws NullPointerException if type or command is null
     */
    <T> void notify(Class<T> type, Procedure<? super T> command);
    
    /**
     * Remove a specific listener interested in type from this registry.
     * If the same listener is also registered for other types,
     * he will still get notified for these. 
     * 
     * @param <T> the generic type
     * @param type the type's class
     * @param listener the listener being removed from this registry
     * @return true if listener was registered for type before
     * @throws NullPointerException if type is null
     */
    <T> boolean remove(Class<T> type, T listener);
    
    /**
     * Removes a listener completely from this registry. 
     * 
     * @param <T> the generic type
     * @param listener the listener being removed
     * @return true if listener was registered before
     * @throws NullPointerException if listener is null
     */
    <T> boolean remove(T listener);
    
    /**
     * Removes a type and its listeners completely from this registry.
     * 
     * @param <T> the generic type
     * @param type the type being removed
     * @return an iterable of all listeners that were registered for that type
     * @throws NullPointerException if type is null
     */
    <T> Iterable<T> removeAll(Class<T> type);
    
}
