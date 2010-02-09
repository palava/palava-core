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
import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;

/**
 * Abstract module for applications.
 *
 * @author Willi Schoenborn
 */
public abstract class ServiceModule extends AbstractModule {

    /**
     * Binds a service key.
     * 
     * @param <S> the generic service type
     * @param key the service key
     * @return a {@link ServiceBinder}
     */
    protected final <S extends Service> ServiceBinder<S> serve(Key<S> key) {
        return new InternalServiceBinder<S>(key);
    }
    
    /**
     * Binds a service key.
     * 
     * @param <S> the generic service type
     * @param type the service type
     * @return a {@link ServiceBinder}
     */
    protected final <S extends Service> ServiceBinder<S> serve(Class<S> type) {
        return serve(Key.get(type));
    }
    
    /**
     * Binds a service literal.
     * 
     * @param <S> the generic service type
     * @param literal the service type literal
     * @return a {@link ServiceBinder}
     */
    protected final <S extends Service> ServiceBinder<S> serve(TypeLiteral<S> literal) {
        return serve(Key.get(literal));
    }
    
    /**
     * Binds a service key to a concrete class.
     * 
     * @param type the service type
     */
    protected final void serveClass(Class<? extends Service> type) {
        serve(type);
        binder().bind(type).in(Singleton.class);
    }

    /**
     * Private implementation of the {@link ServiceBinder} interface
     * which holds a reference to the enclosing {@link Module}.
     *
     * @author Willi Schoenborn
     * @param <S> the generic service type
     */
    private final class InternalServiceBinder<S extends Service> implements ServiceBinder<S> {
        
        private final Key<S> key;
        
        public InternalServiceBinder(Key<S> key) {
            this.key = Preconditions.checkNotNull(key, "Key");
            Multibinder.newSetBinder(binder(), Service.class).addBinding().to(key).in(Singleton.class);
        }
        
        @Override
        public void with(Class<? extends S> serviceKey) {
            binder().bind(key).to(serviceKey).in(Singleton.class);            
        }
        
        @Override
        public void with(Key<? extends S> serviceKey) {
            binder().bind(key).to(serviceKey).in(Singleton.class);       
        }
        
        @Override
        public void with(S service) {
            binder().bind(key).toInstance(service);
        }
        
    }
}
