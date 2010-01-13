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

package de.cosmocode.palava;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jdom.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.internal.Maps;
import com.google.inject.internal.Sets;
import com.google.inject.matcher.Matchers;
import com.google.inject.name.Names;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

import de.cosmocode.palava.core.intercept.LifycycleInjectionListener;
import de.cosmocode.palava.core.service.lifecycle.Disposable;

/**
 * An implementation of the {@link ComponentManager} interface
 * which uses Google Guice to handle dependency injection.
 *
 * @param <S> generic service type to prevent casting
 * @author Willi Schoenborn
 */
public final class GuiceComponentManager<S> implements ComponentManager, Module {

    private static final Logger log = LoggerFactory.getLogger(GuiceComponentManager.class);

    private final Element root;
    private final Server server;
    
    private final Map<String, Map.Entry<Class<S>, Class<S>>> serviceClasses = Maps.newHashMap();
    
    private final Set<Disposable> disposables = Sets.newHashSet();
    private final Set<Service> services = Sets.newHashSet();
    
    private GuiceComponentManager(Element root, @Deprecated Server server) {
        this.root = Preconditions.checkNotNull(root, "Root");
        this.server = Preconditions.checkNotNull(server, "Server");
    }
    
    @Override
    public void configure(Binder binder) {
        parse(binder);
        
        for (Map.Entry<String, Map.Entry<Class<S>, Class<S>>> entry : serviceClasses.entrySet()) {
            final String name = entry.getKey();
            final Class<S> spec = entry.getValue().getKey();
            final Class<S> impl = entry.getValue().getValue();
            
            if (name.equals(spec.getName())) {
                binder.bind(spec).to(impl).in(Singleton.class);
            } else {
                binder.bind(spec).annotatedWith(Names.named(name)).to(impl).in(Singleton.class);
            }
        }
    }
    
    private void parse(Binder binder) {
        log.info("Parsing component configuration");
        
        @SuppressWarnings("unchecked")
        final List<Element> children = root.getChildren();

        for (final Element element : children) {
            final String specName = Preconditions.checkNotNull(element.getAttributeValue("spec"), "spec");
            final String implName = Preconditions.checkNotNull(element.getAttributeValue("impl"), "impl");
            
            final Class<S> spec;
            final Class<S> impl;
            
            try {
                @SuppressWarnings("unchecked")
                final Class<S> specClass = (Class<S>) Class.forName(specName);
                spec = specClass;
                @SuppressWarnings("unchecked")
                final Class<S> implClass = (Class<S>) Class.forName(implName);
                impl = implClass;
            } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException(e);
            }

            final String name = element.getAttributeValue("name", specName);
            
            if (serviceClasses.containsKey(name)) throw new IllegalStateException(name + " alread contained in map");
            serviceClasses.put(name, Maps.<Class<S>, Class<S>>immutableEntry(spec, impl));
            
            binder.bindListener(Matchers.any(), new TypeListener() {
                
                @Override
                public <I> void hear(TypeLiteral<I> literal, TypeEncounter<I> encounter) {
                    encounter.register(new LifycycleInjectionListener(root, server));
                    encounter.register(new InjectionListener<Object>() {
                        
                        @Override
                        public void afterInjection(Object injectee) {
                            if (injectee instanceof Disposable) {
                                final Disposable disposable = Disposable.class.cast(injectee);
                                disposables.add(disposable);
                            } else if (injectee instanceof Service) {
                                final Service service = Service.class.cast(injectee);
                                services.add(service);
                            }
                        }
                        
                    });
                }
                
            });
        }
    }
    
    @Override
    public void initialize() throws Exception {
        for (Map.Entry<String, Map.Entry<Class<S>, Class<S>>> entry : serviceClasses.entrySet()) {
            final String name = entry.getKey();
            final Class<S> spec = entry.getValue().getKey();
            final Object service;
            if (spec.getName().equals(name)) {
                service = lookup(spec);
            } else {
                service = lookup(spec, name);
            }
            if (service instanceof Component) {
                Component.class.cast(service).compose(this);
            }
        }
    }
    
    @Override
    public <T> T lookup(Class<T> spec) {
        return server.getInjector().getInstance(spec);
    }

    @Override
    public <T> T lookup(Class<T> spec, String name) {
        return server.getInjector().getInstance(Key.get(spec, Names.named(name)));
    }

    @Override
    public void shutdown() {
        for (Disposable disposable : disposables) {
            disposable.dispose();
        }
        for (Service service : services) {
            service.shutdown();
        }
    }

    public static GuiceComponentManager<Object> create(Element root, Server server) {
        return new GuiceComponentManager<Object>(root, server);
    }
    
}
