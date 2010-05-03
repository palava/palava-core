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

import com.google.common.base.Preconditions;

import de.cosmocode.collections.Procedure;

/**
 * Abstract implementation of the {@link Registry} interface.
 *
 * @since 2.0
 * @author Willi Schoenborn
 */
public abstract class AbstractRegistry implements Registry {

    @Override
    public <T> void register(Class<T> type, T listener) {
        Preconditions.checkNotNull(type, "Type");
        register(Key.get(type), listener);
    };
    
    @Override
    public <T> Iterable<T> getListeners(Class<T> type) {
        Preconditions.checkNotNull(type, "Type");
        return getListeners(Key.get(type));
    }
    
    @Override
    public <T> T proxy(Class<T> type) {
        Preconditions.checkNotNull(type, "Type");
        return proxy(Key.get(type));
    }
    
    @Override
    public <T> void notify(Class<T> type, Procedure<? super T> command) {
        Preconditions.checkNotNull(type, "Type");
        notify(Key.get(type), command);
    }
    
    @Override
    public <T> void notifySilent(Class<T> type, Procedure<? super T> command) {
        Preconditions.checkNotNull(type, "Type");
        notifySilent(Key.get(type), command);
    }
    
    @Override
    public <T> boolean remove(Class<T> type, T listener) {
        Preconditions.checkNotNull(type, "Type");
        return remove(Key.get(type), listener);
    };
    
    @Override
    public <T> Iterable<T> removeAll(Class<T> type) {
        Preconditions.checkNotNull(type, "Type");
        return removeAll(Key.get(type));
    }

}
