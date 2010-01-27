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

package de.cosmocode.palava.bridge.session;

import java.text.Collator;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import de.cosmocode.json.JSONMapable;
import de.cosmocode.json.JSONRenderer;
import de.cosmocode.palava.bridge.scope.Destroyable;

/**
 * A {@link HttpSession} represents an browser session,
 * lasting for a specific period of time.
 *
 * @author Willi Schoenborn
 */
public interface HttpSession extends Destroyable, JSONMapable {
    
    String LANGUAGE = "lang";
    
    String COUNTRY = "country";

    /**
     * Provide the session id of this session.
     * 
     * @return the session id
     */
    String getSessionId();
    
    /**
     * Binds a key to a specific value.
     * 
     * @param <K> the key type
     * @param <V> the value type
     * @param key the key
     * @param value the value
     * @throws NullPointerException if key is null
     */
    <K, V> void set(K key, V value);
    
    /**
     * Retrieves a value bound to the specified key.
     * 
     * @param <K> the key type
     * @param <V> the value type
     * @param key the key
     * @return the value bound to the key or null if there
     *         is no value for the given key
     */
    <K, V> V get(K key);
    
    /**
     * Checks for the existence of a binding
     * for the specified key. 
     * 
     * @param <K> the key type
     * @param key the key
     * @return true if the key is currently bound to a value
     */
    <K> boolean contains(K key);
    
    /**
     * Removes a key-value binding.
     * 
     * @param <K> the key type
     * @param <V> the value type
     * @param key the key
     * @return the old value if the specified key was bound to a 
     *         value before, null otherwise
     */
    <K, V> V remove(K key);
    
    /**
     * Puts all elements of the specified in this session.
     * 
     * @param <K> the key type
     * @param <V> the value type
     * @param map the map providing key-value bindings
     * @throws NullPointerException if map is null
     */
    <K, V> void putAll(Map<? extends K, ? extends V> map);
    
    /**
     * Provides the date of the last access.
     * 
     * @return the last access date
     */
    Date getAccessTime();
    
    /**
     * Update the access time and set it to the current
     * time stamp.
     */
    void updateAccessTime();
    
    /**
     * Provide the user's locale.
     * 
     * @return the user's locale
     * @throws IllegalStateException if there is no locale information
     *         stored in this session
     */
    Locale getLocale();
    
    /**
     * Provide a locale aware {@link NumberFormat} bound to the 
     * locale of this session as specified by {@link HttpSession#getLocale()}.
     * 
     * @return a {@link NumberFormat} instance of the given user locale
     */
    NumberFormat getNumberFormat();
    
    /**
     * Provide a locale aware {@link Collator} bound to the
     * locale of this session as specified by {@link HttpSession#getLocale()}.
     * 
     * @return a {@link Collator} instance of the given user locale
     */
    Collator getCollator();
    
    /**
     * {@inheritDoc}
     * 
     * An {@link HttpSession} must have the following JSON structure:
     * <pre>
     *  {
     *      id: <id>,
     *      accesstime: <accessTime>,
     *      data: {
     *          key: value,
     *          ...
     *      }
     *  }
     * </pre>
     */
    @Override
    JSONRenderer renderAsMap(JSONRenderer renderer);
    
}
