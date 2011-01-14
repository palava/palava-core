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

import com.google.common.base.Preconditions;
import com.google.inject.Binder;
import com.google.inject.Module;

/**
 * A {@link Module} which binds T to {@link Providers#nullProvider()}.
 *
 * @since 2.10
 * @author Willi Schoenborn
 * @param <T> generic target type
 */
final class NullProviderModule<T> implements Module {

    private final Class<T> type;

    NullProviderModule(Class<T> type) {
        this.type = Preconditions.checkNotNull(type, "Type");
    }

    @Override
    public void configure(Binder binder) {
        binder.bind(type).toProvider(Providers.<T>nullProvider());
    }
    
}
