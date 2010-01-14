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

package de.cosmocode.palava.core.call;

import java.util.Map;

import com.google.common.base.Preconditions;
import com.google.inject.Key;
import com.google.inject.OutOfScopeException;
import com.google.inject.Provider;
import com.google.inject.Scope;
import com.google.inject.internal.Maps;

import de.cosmocode.palava.core.command.filter.Filter;
import de.cosmocode.palava.core.scope.ManagedScope;

/**
 * Custom {@link Scope} implementation for one call.
 *
 * @author Oliver Lorenz
 * @author Willi Schoenborn
 */
final class CallScope implements ManagedScope {

    private final Map<Filter, Map<Key<?>, Object>> context = Maps.newHashMap();
    
    @Override
    public void enter() {
        final Filter filter = null;
        Preconditions.checkState(!context.containsKey(filter), "A scoping block is already in progress");
        final Map<Key<?>, Object> map = Maps.newHashMap();
        context.put(filter, map);
    }

    @Override
    public <T> void seed(Key<T> key, T value) {
        final Map<Key<?>, Object> scopeContext = getScopeContext(key);
        Preconditions.checkState(
            !scopeContext.containsKey(key), 
            "A value for the key %s was already seeded in this scope. Old value: %s New value: %s",
            key, scopeContext.get(key), value
        );
        scopeContext.put(key, value);
    }

    @Override
    public <T> void seed(Class<T> type, T value) {
        seed(Key.get(type), value);
    }

    @Override
    public void exit() {
        final Filter filter = null;
        Preconditions.checkState(context.containsKey(filter), "No scoping block in progress");
        context.remove(filter);
    }

    @Override
    public <T> Provider<T> scope(final Key<T> key, final Provider<T> provider) {
        return new Provider<T>() {
            
            @Override
            public T get() {
                final Map<Key<?>, Object> scopeContext = getScopeContext(key);
                
                @SuppressWarnings("unchecked")
                T current = (T) scopeContext.get(key);
                if (current == null && !scopeContext.containsKey(key)) {
                    current = provider.get();
                    scopeContext.put(key, current);
                }
                return current;
            }
            
        };
    }

    private <T> Map<Key<?>, Object> getScopeContext(Key<T> key) {
        final Filter filter = null;
        final Map<Key<?>, Object> scopeContext = context.get(filter);
        if (scopeContext == null) {
            throw new OutOfScopeException("Cannot access " + key + " outside of a scoping block");
        } else {
            return scopeContext;
        }
    }

}
