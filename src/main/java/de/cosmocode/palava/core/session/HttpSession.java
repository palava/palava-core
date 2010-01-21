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

package de.cosmocode.palava.core.session;

import java.text.Collator;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import de.cosmocode.json.JSONMapable;
import de.cosmocode.json.JSONRenderer;
import de.cosmocode.palava.core.scope.Destroyable;

/**
 * A {@link HttpSession} represents an browser session,
 * lasting for a specific period of time.
 *
 * @author Willi Schoenborn
 */
public interface HttpSession extends Destroyable, JSONMapable {
    
    String LANGUAGE = "lang";
    
    String COUNTRY = "country";

    String getSessionId();
    
    <K, V> void set(K key, V value);
    
    <K, V> V get(K key);
    
    <K> boolean contains(K key);
    
    <K, V> V remove(K key);
    
    <K, V> void putAll(Map<? extends K, ? extends V> map);
    
    Date getAccessTime();
    
    void updateAccessTime();
    
    Locale getLocale();
    
    NumberFormat getNumberFormat();
    
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
