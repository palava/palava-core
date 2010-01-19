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

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import com.google.inject.internal.UniqueAnnotations;
import com.google.inject.multibindings.Multibinder;

import de.cosmocode.palava.core.CoreModule;
import de.cosmocode.palava.core.call.filter.Filter;
import de.cosmocode.palava.core.call.filter.definition.FilterDefinition;
import de.cosmocode.palava.core.call.filter.definition.NamePatternMatcher;
import de.cosmocode.palava.core.call.filter.definition.NamePatternType;
import de.cosmocode.palava.core.command.Alias;
import de.cosmocode.palava.core.command.Aliases;
import de.cosmocode.palava.core.service.Service;

/**
 * Abstract module for applications.
 *
 * @author Willi Schoenborn
 */
public abstract class AbstractApplicationModule extends AbstractModule {
    
    private final List<FilterDefinition> filterDefinitions = Lists.newArrayList();
    private final Map<Key<Filter>, Filter> filterInstances = Maps.newLinkedHashMap();
    
    @Override
    protected final void configure() {
        configureApplication();
        
        for (Map.Entry<Key<Filter>, Filter> entry : filterInstances.entrySet()) {
            bind(entry.getKey()).toInstance(entry.getValue());
        }
        
        final TypeLiteral<List<FilterDefinition>> literal = new TypeLiteral<List<FilterDefinition>>() { };
        bind(Key.get(literal, UniqueAnnotations.create())).toInstance(filterDefinitions);
    }
    
    /**
     * Configures the applications.
     * 
     * <p>
     *   It required to install implementations for all
     *   core interfaces, e.g. by installing {@link CoreModule}.
     * </p>
     */
    protected abstract void configureApplication();

    /**
     * Binds a service key.
     * 
     * @param <S> the generic service type
     * @param key the service key
     * @return a {@link ServiceBinder}
     */
    protected <S extends Service> ServiceBinder<S> serve(Key<S> key) {
        return new InternalServiceBinder<S>(key);
    }
    
    /**
     * Binds a service key.
     * 
     * @param <S> the generic service type
     * @param type the service type
     * @return a {@link ServiceBinder}
     */
    protected <S extends Service> ServiceBinder<S> serve(Class<S> type) {
        return serve(Key.get(type));
    }
    
    /**
     * Binds a filter.
     * 
     * @param pattern the name pattern the filter will be applied on
     * @param patterns additional patterns
     * @return a {@link FilterBinder}
     */
    protected FilterBinder filter(String pattern, String... patterns) {
        final List<String> list = Lists.newArrayList(pattern);
        Collections.addAll(list, patterns);
        return new InternalFilterBinder(list, NamePatternType.SIMPLE);
    }
    
    /**
     * Binds a filter.
     * 
     * @param pattern the name pattern the filter will be applied on
     * @param patterns additional patterns
     * @return a {@link FilterBinder}
     */
    protected FilterBinder filterRegex(String pattern, String... patterns) {
        final List<String> list = Lists.newArrayList(pattern);
        Collections.addAll(list, patterns);
        return new InternalFilterBinder(list, NamePatternType.REGEX);
    }
    
    /**
     * Binds an alias.
     * 
     * @param packageName the package name the alias stands for
     * @return an {@link AliasBinder}
     */
    protected AliasBinder alias(String packageName) {
        return new InternalAliasBinder(packageName);
    }
    
    /**
     * Private implementation of the {@link ServiceBinder} interface
     * which holds a reference to the enclosing {@link Module}.
     *
     * @author Willi Schoenborn
     * @param <S> the generic service type
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
     * Private implementation of the {@link FilterBinder} interface
     * which holds a reference to the enclosing {@link Module}.
     *
     * @author Willi Schoenborn
     * @param <F> the generic filter type
     */
    private class InternalFilterBinder implements FilterBinder {
        
        private final List<String> patterns;
        
        private final NamePatternType patternType;
        
        public InternalFilterBinder(List<String> patterns, NamePatternType patternType) {
            this.patterns = Preconditions.checkNotNull(patterns, "Patterns");
            this.patternType = Preconditions.checkNotNull(patternType, "PatternType");
        }
        
        @Override
        public void through(Class<? extends Filter> type) {
            through(Key.get(type));
        }
        
        @Override
        public void through(Key<? extends Filter> filterKey) {
            for (String pattern : patterns) {
                final FilterDefinition definition = new InternalFilterDefinition(pattern, filterKey);
                filterDefinitions.add(definition);
            }
        }
        
        /**
         * Super (!) private implementation of the {@link FilterDefinition} interface
         * which holds a reference to the enclosing {@link Module} to add
         * bound filters.
         *
         * @author Willi Schoenborn
         */
        private class InternalFilterDefinition implements FilterDefinition {
            
            private final NamePatternMatcher matcher;
            private final Key<? extends Filter> key;
            
            public InternalFilterDefinition(String pattern, Key<? extends Filter> key) {
                this.matcher = patternType.matcher(pattern);
                this.key = Preconditions.checkNotNull(key, "Key");
            }
            
            @Override
            public boolean appliesTo(String name) {
                return matcher.matches(name);
            }
            
            @Override
            public Key<? extends Filter> getKey() {
                return key;
            }
            
        }

        @Override
        public void through(Filter filter) {
            Preconditions.checkNotNull(filter, "Filter");
            final Key<Filter> key = Key.get(Filter.class, UniqueAnnotations.create());
            filterInstances.put(key, filter);
            through(key);
        }
        
    }
    
    /**
     * Private implementation of the {@link AliasBinder} interface.
     *
     * @author Willi Schoenborn
     */
    private class InternalAliasBinder implements AliasBinder {
        
        private final String packageName;
        
        public InternalAliasBinder(String packageName) {
            this.packageName = Preconditions.checkNotNull(packageName, "PackageName");
        }
        
        @Override
        public void as(String alias) {
            Multibinder.newSetBinder(binder(), Alias.class).addBinding().toInstance(
                Aliases.of(alias, packageName)
            );
        }
        
    }
    
}
