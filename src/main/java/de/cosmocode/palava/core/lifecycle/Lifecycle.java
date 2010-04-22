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
