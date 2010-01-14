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

package de.cosmocode.palava.core.service;

import com.google.inject.Inject;

/**
 * A manager for all services running inside the palava
 * framework. The {@link ServiceManager} is responsible for
 * the proper execution of all lifecycle methods
 * as defined in the lifecycle package.
 *
 * @author Willi Schoenborn
 */
public interface ServiceManager {

    /**
     * Lookups a service by its specification.
     * 
     * @deprecated legacy way to retrieve a Service instance Use the {@link Inject}
     * annotation instead.
     * 
     * @param <T> the generic interface type
     * @param spec the spec class literal
     * @return the found Service
     * @throws NullPointerException if spec is null
     */
    @Deprecated
    <T> T lookup(Class<T> spec);

    /**
     * Lookups a service by its specification and a its name.
     * 
     * @deprecated legacy way to retrieve a Service instance Use the {@link Inject}
     * annotation instead.
     * 
     * @param <T> the generic interface type
     * @param spec the spec class literal
     * @param name the service name
     * @return the found Service
     * @throws NullPointerException if spec or name is null
     */
    @Deprecated
    <T> T lookup(Class<T> spec, String name);
    
    /**
     * Shuts down this {@link ServiceManager} and its
     * services.
     */
    void shutdown();
    
}
