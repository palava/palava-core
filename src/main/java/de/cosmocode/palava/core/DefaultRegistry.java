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

import de.cosmocode.collections.Procedure;

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
    public <T> Iterable<T> getListeners(Key<T> key) {
        Preconditions.checkNotNull(key, "Key");
        @SuppressWarnings("unchecked")
        final Iterable<T> listeners = (Iterable<T>) services.get(key);
        return Iterables.unmodifiableIterable(listeners);
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
                return String.format("%s.proxy(%s)", DefaultRegistry.this, key);
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
            final int prime = 31;
            int result = 1;
            result = prime * result + getOuterType().hashCode();
            result = prime * result + ((key == null) ? 0 : key.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (!(obj instanceof ProxyHandler<?>)) {
                return false;
            }
            final ProxyHandler<?> other = (ProxyHandler<?>) obj;
            if (!getOuterType().equals(other.getOuterType())) {
                return false;
            }
            if (key == null) {
                if (other.key != null) {
                    return false;
                }
            } else if (!key.equals(other.key)) {
                return false;
            }
            return true;
        }

        private DefaultRegistry getOuterType() {
            return DefaultRegistry.this;
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
