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

package de.cosmocode.palava.core.scope;

import java.util.Map;

import com.google.common.base.Preconditions;
import com.google.inject.Key;
import com.google.inject.OutOfScopeException;
import com.google.inject.Provider;
import com.google.inject.Scope;
import com.google.inject.internal.Maps;

/**
 * Custom {@link Scope} implementation for one call.
 *
 * @author Willi Schoenborn
 */
final class CallScope implements ManagedScope {

    private final ThreadLocal<Map<Key<?>, Object>> scoped = new ThreadLocal<Map<Key<?>, Object>>();

    @Override
    public void enter() {
        final Map<Key<?>, Object> map = Maps.newHashMap();
        scoped.set(map);
    }

    @Override
    public <T> void seed(Key<T> key, T value) {
        Preconditions.checkNotNull(key, "Key");
        Preconditions.checkNotNull(value, "Value");
        final Map<Key<?>, Object> scopedContext = getScopedContext(key);
        Preconditions.checkState(!scopedContext.containsKey(key), 
            "A value for the key %s was already seeded in this scope. Old value: %s New value: %s",
            key, scopedContext.get(key), value
        );
    }

    @Override
    public <T> void seed(Class<T> type, T value) {
        Preconditions.checkNotNull(type, "Type");
        Preconditions.checkNotNull(value, "Value");
        seed(Key.get(type), value);
    }

    @Override
    public void exit() {
        scoped.remove();
    }

    @Override
    public <T> Provider<T> scope(final Key<T> key, final Provider<T> provider) {
        Preconditions.checkNotNull(key, "Key");
        Preconditions.checkNotNull(provider, "Provider");
        return new Provider<T>() {
            
            @Override
            public T get() {
                final Map<Key<?>, Object> scopedContext = getScopedContext(key);
                @SuppressWarnings("unchecked")
                final T current = (T) scopedContext.get(key);
                
                if (current == null && !scopedContext.containsKey(key)) {
                    final T unscoped = provider.get();
                    scopedContext.put(key, unscoped);
                    return unscoped;
                } else {
                    return current;
                }
                
            }
            
        };
    }
    
    private <T> Map<Key<?>, Object> getScopedContext(Key<T> key) {
        final Map<Key<?>, Object> map = scoped.get();
        if (map == null) {
            final String message = String.format("Can't access %s outside a call scope block", key);
            throw new OutOfScopeException(message);
        }
        return map;
    }

}
