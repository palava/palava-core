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

package de.cosmocode.palava.bridge.call.filter.definition;

import com.google.inject.Key;

import de.cosmocode.palava.bridge.call.filter.Filter;
import de.cosmocode.palava.bridge.command.Command;

/**
 * A filter definition is the combination of a key exactly defining a
 * filter and the abbility to decide whether the associated filter
 * should run on a specified command.
 *
 * @author Willi Schoenborn
 */
public interface FilterDefinition {

    /**
     * The {@link Key} of the associated filter.
     * 
     * @return the key
     */
    Key<? extends Filter> getKey();
    
    /**
     * Checks whether this filter should run on the specified command.
     *
     * <p>
     *   <strong>Note</strong>: Implementations should be as fast as
     *   possible. Long running operations will affect server performance
     *   and throughput dramatically.
     * </p>
     * 
     * @param command the requested command
     * @return true if this filter should be applied to the incoming call
     *         requesting the specified command
     */
    boolean appliesTo(Command command);
    
}
