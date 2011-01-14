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

import javax.annotation.Nonnull;

import com.google.inject.Module;
import com.google.inject.Provider;

/**
 * Static utility class for {@link Module}s.
 *
 * @since 2.10
 * @author Willi Schoenborn
 */
public final class MoreModules {

    private MoreModules() {
        
    }
    
    /**
     * Creates a module which binds T to a null-returning {@link Provider}.
     *
     * @since 2.10
     * @param <T> generic provider type
     * @param type the target type
     * @return a module which binds a null provider
     * @throws NullPointerException if type is null
     */
    public static <T> Module nullProviderOf(@Nonnull Class<T> type) {
        return new NullProviderModule<T>(type);
    }
    
}
