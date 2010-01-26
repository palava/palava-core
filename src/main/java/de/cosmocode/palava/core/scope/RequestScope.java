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

import com.google.common.base.Preconditions;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.Scope;

import de.cosmocode.palava.core.request.HttpRequest;

/**
 * Custom {@link Scope} implementation for one http request.
 * 
 * @author Oliver Lorenz
 * @author Willi Schoenborn
 */
final class RequestScope implements Scope {

    @Override
    public <T> Provider<T> scope(final Key<T> key, final Provider<T> provider) {
        Preconditions.checkNotNull(key, "Key");
        Preconditions.checkNotNull(provider, "Provider");
        final HttpRequest request = Scopes.getCurrentRequest();
        return new Provider<T>() {
            
            @Override
            public T get() {
                synchronized (request) {
                    final T current = request.<Key<T>, T>get(key);
                    if (current == null && !request.contains(key)) {
                        final T unscoped = provider.get();
                        request.set(key, unscoped);
                        return unscoped;
                    } else {
                        return current;
                    }
                }
            }
            
        };
    }

}
