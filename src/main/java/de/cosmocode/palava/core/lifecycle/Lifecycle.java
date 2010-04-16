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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package de.cosmocode.palava.core.lifecycle;

import com.google.common.base.Preconditions;

/**
 * Static utitlity class for lifecycle interfaces.
 *
 * @since 2.3
 * @author Willi Schoenborn
 */
public final class Lifecycle {

    private Lifecycle() {
        
    }
    
    /**
     * Checks for {@link Initializable}, {@link Disposable} and {@link Startable}.
     * 
     * @since 2.3
     * @param service the service to check
     * @return true if the given service implements at least one of the three interfaces, false otherwise
     * @throws NullPointerException if service is null
     */
    public static boolean hasInterface(Object service) {
        Preconditions.checkNotNull(service, "Service");
        return isInterface(service.getClass());
    }
    
    /**
     * Checks for {@link Initializable}, {@link Disposable} and {@link Startable}.
     * 
     * @since 2.3
     * @param type the type to check
     * @return true if the given type is a subclass of at least one of the three interfaces, false otherwise
     * @throws NullPointerException if type is null
     */
    public static boolean isInterface(Class<?> type) {
        Preconditions.checkNotNull(type, "Type");
        return Initializable.class.isAssignableFrom(type) ||
            Disposable.class.isAssignableFrom(type) ||
            Startable.class.isAssignableFrom(type);
    }

}
