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

import com.google.common.base.Function;

/**
 * A {@link Procedure} is comparable to the {@link Function}
 * interface but returns void instead of a value.
 *
 * @author Willi Schoenborn
 * @param <T> the generic parameter type
 */
public interface Procedure<T> {

    /**
     * Applies this procedure on an object of type T. 
     * 
     * @param input the single parameter for this procedure
     */
    void apply(T input);
    
}
