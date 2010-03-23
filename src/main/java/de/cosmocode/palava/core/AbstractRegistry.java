/**
 * palava - a java-php-bridge
 * Copyright (C) 2007-2010  CosmoCode GmbH
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

package de.cosmocode.palava.core;

import com.google.common.base.Preconditions;

import de.cosmocode.collections.Procedure;

/**
 * Abstract implementation of the {@link Registry} interface.
 *
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
