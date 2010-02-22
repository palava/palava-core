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

/**
 * Default implementation of the {@link Registry} interface.
 *
 * @author Willi Schoenborn
 */
final class DefaultRegistry extends AbstractRegistry {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultRegistry.class);
    
    private static final Method TO_STRING;
    private static final Method EQUALS;
    private static final Method HASHCODE;
    
    static {
        try {
            TO_STRING = Object.class.getMethod("toString");
            EQUALS = Object.class.getMethod("equals", Object.class);
            HASHCODE = Object.class.getMethod("hashCode");
        } catch (NoSuchMethodException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private final Multimap<Key<? extends Object>, Object> services;
    
    public DefaultRegistry() {
        final SetMultimap<Key<? extends Object>, Object> multimap = LinkedHashMultimap.create();
        this.services = Multimaps.synchronizedSetMultimap(multimap);
    }

    @Override
    public <T> void register(Registry.Key<T> key, T listener) {
        Preconditions.checkNotNull(key, "Key");
        Preconditions.checkNotNull(listener, "Listener");
        LOG.trace("registering {} for {}", listener, key);
        synchronized (services) {
            services.put(key, listener);
        }
    };

    @Override
    @SuppressWarnings("unchecked")
    public <T> Iterable<T> getListeners(Key<T> key) {
        Preconditions.checkNotNull(key, "Key");
        return Iterables.unmodifiableIterable((Iterable<T>) services.get(key));
    }
    
    /**
     * Inner class allowing to encapsulate proxy invocation handling.
     *
     * @author Willi Schoenborn
     * @param <T>
     */
    private final class ProxyHandler<T> implements InvocationHandler {
        
        private final Key<T> key;
        
        public ProxyHandler(Key<T> key) {
            this.key = Preconditions.checkNotNull(key, "Key");
        }
        
        @Override
        public Object invoke(Object proxy, final Method method, final Object[] args) 
            throws IllegalAccessException, InvocationTargetException {
            if (method.equals(TO_STRING)) {
                return String.format("Registry.proxy(%s)", key);
            } else if (method.equals(EQUALS)) {
                return equals(args[0]);
            } else if (method.equals(HASHCODE)) {
                return hashCode();
            } else if (method.getReturnType() == void.class) {
                DefaultRegistry.this.notify(key, new Procedure<T>() {
                    
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

        @Override
        public int hashCode() {
            return super.hashCode() + key.hashCode();
        }
        
        @Override
        public boolean equals(Object that) {
            return that.equals(key);
        }
        
    }
    
    @Override
    public <T> T proxy(final Key<T> key) {
        Preconditions.checkNotNull(key, "Key");
        Preconditions.checkArgument(key.getType().isInterface(), "Type must be an interface");
        Preconditions.checkArgument(!key.getType().isAnnotation(), "Type must not be an annotation");
        
        final ClassLoader loader = getClass().getClassLoader();
        final Class<?>[] interfaces = {key.getType()};
        final InvocationHandler handler = new ProxyHandler<T>(key);
        
        @SuppressWarnings("unchecked")
        final T proxy = (T) Proxy.newProxyInstance(loader, interfaces, handler);
        LOG.debug("Created proxy for {}", key);
        return proxy;
    }

    @Override
    public <T> void notify(Key<T> key, Procedure<? super T> command) {
        Preconditions.checkNotNull(key, "Key");
        Preconditions.checkNotNull(command, "Command");
        LOG.trace("notifying all listeners for {} using {}", key, command);
        for (T listener : getListeners(key)) {
            LOG.trace("notifying {} for {}", listener, key);
            command.apply(listener);
        }
    }

    @Override
    public <T> void notifySilent(Key<T> key, Procedure<? super T> command) {
        Preconditions.checkNotNull(key, "Key");
        Preconditions.checkNotNull(command, "Command");
        LOG.trace("notifying all listeners for {} using {}", key, command);
        for (T listener : getListeners(key)) {
            LOG.trace("notifying {} for {}", listener, key);
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
    public <T> boolean remove(Key<T> key, T listener) {
        Preconditions.checkNotNull(key, "Key");
        Preconditions.checkNotNull(listener, "Listener");
        LOG.trace("removing {} from {}", listener, key);
        synchronized (services) {
            return services.remove(key, listener);
        }    
    };

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
    public <T> Iterable<T> removeAll(Key<T> key) {
        Preconditions.checkNotNull(key, "Key");
        LOG.trace("removing all listeners from {}", key);
        synchronized (services) {
            return (Iterable<T>) services.removeAll(key);
        }
    }

}
