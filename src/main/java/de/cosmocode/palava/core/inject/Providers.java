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

package de.cosmocode.palava.core.inject;

import com.google.inject.Provider;

/**
 * Static utility class for {@link Provider}s.
 *
 * @since 2.4
 * @author Willi Schoenborn
 */
public final class Providers {
    
    private Providers() {
        
    }

    /**
     * Creates a {@link Provider} of type T which always returns null.
     * 
     * @param <T> generic return type
     * @return a provider which always returns null
     */
    @SuppressWarnings("unchecked")
    public static <T> Provider<T> nullProvider() {
        return (Provider<T>) NullProvider.INSTANCE;
    }
    
    /**
     * {@link Provider} implementation which always returns null.
     *
     * @author Willi Schoenborn
     * @param <T> generic return type
     */
    private static enum NullProvider implements Provider<Object> {
        
        INSTANCE;
        
        @Override
        public Object get() {
            return null;
        }
        
    }
    
}
