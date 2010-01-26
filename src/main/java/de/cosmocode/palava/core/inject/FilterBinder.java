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

package de.cosmocode.palava.core.inject;

import com.google.inject.Key;

import de.cosmocode.palava.core.call.filter.Filter;
import de.cosmocode.palava.core.call.filter.definition.FilterDefinition;

/**
 * Binds a filter to a key/class/instance.
 *
 * @author Willi Schoenborn
 */
public interface FilterBinder {

    /**
     * Binds the associated predicate and the specified class
     * to a new {@link FilterDefinition}.
     * 
     * @param filterKey the key
     */
    void through(Class<? extends Filter> filterKey);

    /**
     * Binds the associated predicate and the specified key
     * to a new {@link FilterDefinition}.
     * 
     * @param filterKey the key
     */
    void through(Key<? extends Filter> filterKey);
    
    /**
     * Binds the associated predicate to the specifed instance.
     * 
     * @param filter the target filter
     */
    void through(Filter filter);
    
}
