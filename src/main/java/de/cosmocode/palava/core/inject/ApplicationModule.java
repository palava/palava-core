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

package de.cosmocode.palava.core.inject;

import java.util.regex.Pattern;

import com.google.common.base.Preconditions;
import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.multibindings.Multibinder;

import de.cosmocode.palava.core.call.filter.Filter;
import de.cosmocode.palava.core.service.Service;

/**
 * 
 *
 * @author Willi Schoenborn
 */
public abstract class ApplicationModule extends AbstractModule {

    /**
     * 
     * @param <S>
     * @param key
     * @return
     */
    protected <S extends Service> ServiceBinder<S> serve(Key<S> key) {
        return new InternalServiceBinder<S>(key);
    }
    
    /**
     * 
     * @param <S>
     * @param type
     * @return
     */
    protected <S extends Service> ServiceBinder<S> serve(Class<S> type) {
        return serve(Key.get(type));
    }
    
    /**
     * 
     * @param <F>
     * @param name
     * @param names
     * @return
     */
    protected <F extends Filter> FilterBinder<F> filter(String name, String... names) {
        // TODO fix name
        return new InternalFilterBinder<F>(null);
    }
    
    /**
     * 
     * @param <F>
     * @param pattern
     * @param patterns
     * @return
     */
    protected <F extends Filter> FilterBinder<F> filter(Pattern pattern, Pattern... patterns) {
        return new InternalFilterBinder<F>(null);
    }
    
    /**
     * 
     *
     * @author Willi Schoenborn
     * @param <S>
     */
    private class InternalServiceBinder<S extends Service> implements ServiceBinder<S> {
        
        private final Key<S> key;
        
        public InternalServiceBinder(Key<S> key) {
            this.key = Preconditions.checkNotNull(key, "Key");
            Multibinder.newSetBinder(binder(), Service.class).addBinding().to(key);
        }
        
        @Override
        public void with(Class<? extends S> serviceKey) {
            binder().bind(key).to(serviceKey);            
        }
        
        @Override
        public void with(Key<? extends S> serviceKey) {
            binder().bind(key).to(serviceKey);       
        }
        
        @Override
        public void with(S service) {
            binder().bind(key).toInstance(service);
        }
        
    }
    
    /**
     * 
     *
     * @author Willi Schoenborn
     * @param <F>
     */
    private class InternalFilterBinder<F extends Filter> implements FilterBinder<F> {
        
        private final Key<F> key;
        
        public InternalFilterBinder(Key<F> key) {
            this.key = Preconditions.checkNotNull(key, "Key");
            Multibinder.newSetBinder(binder(), Filter.class).addBinding().to(key);
        }
        
        @Override
        public void through(Class<? extends F> filterKey) {
            binder().bind(key).to(filterKey);
        }
        
        @Override
        public void through(Key<? extends F> filterKey) {
            binder().bind(key).to(filterKey);
        }
        
        @Override
        public void through(F filter) {
            binder().bind(key).toInstance(filter);
        }
        
    }
    
}
