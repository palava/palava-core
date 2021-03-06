/**
 * Copyright 2010 CosmoCode GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.cosmocode.palava.core;

import com.google.inject.ConfigurationException;
import com.google.inject.Key;
import com.google.inject.ProvisionException;

import de.cosmocode.commons.Stateful;
import de.cosmocode.palava.core.event.FrameworkStart;
import de.cosmocode.palava.core.event.FrameworkStop;
import de.cosmocode.palava.core.event.PostFrameworkStart;
import de.cosmocode.palava.core.event.PostFrameworkStop;
import de.cosmocode.palava.core.event.PreFrameworkStart;
import de.cosmocode.palava.core.event.PreFrameworkStop;

/**
 * Root type for the palava framework.
 *
 * @since 2.0
 * @author Willi Schoenborn
 */
public interface Framework extends Stateful {

    /**
     * Starts the framework by firing the {@link PreFrameworkStart},
     * {@link FrameworkStart} and {@link PostFrameworkStart} events.
     * 
     * <p>
     *   Calling this method when the framework is starting or already started
     *   has no effect.
     * </p>
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
     * Stops the framework by firing the {@link PreFrameworkStop},
     * {@link FrameworkStop} and {@link PostFrameworkStop} events.
     * 
     * <p>
     *   Calling this method when the framework is stopping or already terminated
     *   has no effect.
     * </p>
     */
    void stop();

}
