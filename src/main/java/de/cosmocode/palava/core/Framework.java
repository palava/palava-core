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

package de.cosmocode.palava.core;

import com.google.inject.ConfigurationException;
import com.google.inject.Key;
import com.google.inject.ProvisionException;

import de.cosmocode.commons.Stateful;

/**
 * Root type for the palava framework.
 *
 * @author Willi Schoenborn
 */
public interface Framework extends Stateful {

    /**
     * Starts the framework.
     */
    void start();
    
    /**
     * Retrieves an initialized bound instance of type T.
     * 
     * @param <T> the generic type parameter
     * @param type the class literal of type t
     * @return an instance of T
     * @throws ConfigurationException if no binding exists for type T
     * @throws ProvisionException if there was a runtime failure while providing an instance
     */
    <T> T getInstance(Class<T> type) throws ConfigurationException, ProvisionException;
    
    /**
     * Retrieves an initialized bound instance of type T.
     * 
     * @param <T> the generic type parameter
     * @param key the binding key
     * @return an instance of T
     * @throws ConfigurationException if no binding exists for the given key
     * @throws ProvisionException if there was a runtime failure while providing an instance
     */
    <T> T getInstance(Key<T> key) throws ConfigurationException, ProvisionException;
    
    /**
     * Stops the framework.
     */
    void stop();

}
