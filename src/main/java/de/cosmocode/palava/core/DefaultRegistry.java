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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;
import com.google.inject.Singleton;

/**
 * Default implementation of the {@link Registry} interface.
 *
 * @author Willi Schoenborn
 */
@Singleton
final class DefaultRegistry implements Registry {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultRegistry.class);

    private final Multimap<Class<? extends Object>, Object> services;
    
    public DefaultRegistry() {
        final SetMultimap<Class<? extends Object>, Object> multimap = LinkedHashMultimap.create();
        this.services = Multimaps.synchronizedSetMultimap(multimap);
    }

    @Override
    public <T> void register(Class<T> type, T listener) {
        Preconditions.checkNotNull(type, "Type");
        Preconditions.checkNotNull(listener, "Listener");
        LOG.trace("registering {} for {}", listener, type);
        synchronized (services) {
            services.put(type, listener);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Iterable<T> getListeners(Class<T> type) {
        Preconditions.checkNotNull(type, "Type");
        return Iterables.unmodifiableIterable((Iterable<T>) services.get(type));
    }
    
    @Override
    public <T> T proxy(final Class<T> type) {
        Preconditions.checkNotNull(type, "Type");
        Preconditions.checkArgument(type.isInterface(), "Type must be an interface");
        Preconditions.checkArgument(!type.isAnnotation(), "Type must not be an annotation");
        
        final ClassLoader loader = getClass().getClassLoader();
        final Class<?>[] interfaces = {type};
        final InvocationHandler handler = new InvocationHandler() {
            
            @Override
            public Object invoke(Object proxy, final Method method, final Object[] args) 
                throws IllegalAccessException, InvocationTargetException {
                if (method.getReturnType() == void.class) {
                    DefaultRegistry.this.notify(type, new Procedure<T>() {
                        
                        public void apply(T listener) {
                            try {
                                method.invoke(listener, args);
                            } catch (IllegalAccessException e) {
                                throw new IllegalStateException(e);
                            } catch (InvocationTargetException e) {
                                throw new IllegalStateException(e);
                            }
                        };
                        
                    });
                    return null;
                } else {
                    final String message = String.format("%s must return void", method);
                    throw new IllegalStateException(message);
                }
            }
            
        };
        
        @SuppressWarnings("unchecked")
        final T proxy = (T) Proxy.newProxyInstance(loader, interfaces, handler);
        LOG.debug("Created proxy for {}", type);
        return proxy;
    }

    @Override
    public <T> void notify(Class<T> type, Procedure<? super T> command) {
        Preconditions.checkNotNull(type, "Type");
        Preconditions.checkNotNull(command, "Command");
        LOG.trace("notifying all listeners for {} using {}", type, command);
        for (T listener : getListeners(type)) {
            LOG.trace("notifying {} for {}", listener, type);
            command.apply(listener);
        }
    }

    @Override
    public <T> void notifySilent(Class<T> type, Procedure<? super T> command) {
        Preconditions.checkNotNull(type, "Type");
        Preconditions.checkNotNull(command, "Command");
        LOG.trace("notifying all listeners for {} using {}", type, command);
        for (T listener : getListeners(type)) {
            LOG.trace("notifying {} for {}", listener, type);
            try {
                command.apply(listener);
            /*CHECKSTYLE:OFF*/
            } catch (RuntimeException e) {
            /*CHECKSTYLE:ON*/
                LOG.error("Notifying listener failed", e);
            }
        }
    }

    @Override
    public <T> boolean remove(Class<T> type, T listener) {
        Preconditions.checkNotNull(type, "Type");
        Preconditions.checkNotNull(listener, "Listener");
        LOG.trace("removing {} from {}", listener, type);
        synchronized (services) {
            return services.remove(type, listener);
        }
    }

    @Override
    public <T> boolean remove(T listener) {
        Preconditions.checkNotNull(listener, "Listener");
        LOG.trace("removing {}", listener);
        synchronized (services) {
            return services.values().removeAll(ImmutableSet.of(listener));
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Iterable<T> removeAll(Class<T> type) {
        Preconditions.checkNotNull(type, "Type");
        LOG.trace("removing all listeners from {}", type);
        synchronized (services) {
            return (Iterable<T>) services.removeAll(type);
        }
    }

}
