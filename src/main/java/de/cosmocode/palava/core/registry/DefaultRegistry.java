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

package de.cosmocode.palava.core.registry;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.inject.Singleton;

/**
 * Default implementation of the {@link Registry} interface.
 *
 * @author Willi Schoenborn
 */
@Singleton
final class DefaultRegistry implements Registry {

    private final Multimap<Class<? extends Object>, Object> services = HashMultimap.create();

    @Override
    public <T> void register(Class<T> type, T listener) {
        Preconditions.checkNotNull(type, "Type");
        Preconditions.checkNotNull(listener, "Listener");
        services.put(type, listener);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Iterable<T> getListeners(Class<T> type) {
        Preconditions.checkNotNull(type, "Type");
        return (Iterable<T>) services.get(type);
    }
    
    @Override
    public <T> void notify(Class<T> type, Procedure<? super T> command) {
        Preconditions.checkNotNull(type, "Type");
        Preconditions.checkNotNull(command, "Command");
        for (T listener : getListeners(type)) {
            command.apply(listener);
        }
    }

    @Override
    public <T> boolean remove(Class<T> type, T listener) {
        Preconditions.checkNotNull(type, "Type");
        Preconditions.checkNotNull(listener, "Listener");
        return services.remove(type, listener);
    }

    @Override
    public <T> boolean remove(T listener) {
        Preconditions.checkNotNull(listener, "Listener");
        return services.values().remove(listener);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Iterable<T> removeAll(Class<T> type) {
        Preconditions.checkNotNull(type, "Type");
        return (Iterable<T>) services.removeAll(type);
    }

}
