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

package de.cosmocode.palava.core.service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.inject.Binder;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.Scopes;
import com.google.inject.Singleton;
import com.google.inject.name.Names;

import de.cosmocode.palava.core.service.lifecycle.Config;
import de.cosmocode.palava.core.service.lifecycle.Configurable;
import de.cosmocode.palava.core.service.lifecycle.Disposable;
import de.cosmocode.palava.core.service.lifecycle.Initializable;
import de.cosmocode.palava.core.service.lifecycle.LifecycleException;
import de.cosmocode.palava.core.service.lifecycle.Startable;

/**
 * Default implementation of the {@link ServiceManager} interface.
 *
 * @author Willi Schoenborn
 */
@Singleton
final class DefaultServiceManager implements ServiceManager {
    
    private static final Logger log = LoggerFactory.getLogger(DefaultServiceManager.class);
    
    private static final String FILE = "file";
    private static final String SPEC = "spec";
    private static final String IMPL = "impl";
    private static final String NAME = "name";

    private final Injector injector;
    
    private final Element root;
    
    private final Map<Key<Object>, Element> configuration = Maps.newHashMap();
    private final BiMap<Key<Object>, Object> services = HashBiMap.create();
    
    @Inject
    public DefaultServiceManager(Injector parentInjector, @Config(ServiceModule.class) Element config) {
        Preconditions.checkNotNull(parentInjector, "Injector");
        
        Preconditions.checkNotNull(config, "Configuration");
        final String path = config.getChildText(FILE);
        Preconditions.checkNotNull(path, "Service config file path not set");
        
        final File file = new File(path);
        Preconditions.checkState(file.exists(), "Service config file does not exist");

        try {
            this.root = new SAXBuilder().build(file).getRootElement();
        } catch (JDOMException e) {
            throw new IllegalArgumentException(e);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
        
        this.injector = parentInjector.createChildInjector(new IntegratedServiceModule());
        
        for (Key<Object> key : configuration.keySet()) {
            final Object service = injector.getInstance(key);
            log.debug("Created service {}", service);
            services.put(key, service);
        }
        
        configure();
        initialize();
        start();
    }
    
    /**
     * Integrated module to simplify service bindings.
     *
     * @author Willi Schoenborn
     */
    private class IntegratedServiceModule implements Module {
        
        @Override
        public void configure(Binder binder) {

            @SuppressWarnings("unchecked")
            final List<Element> children = root.getChildren();
            
            for (Element child : children) {
                final String specName = child.getAttributeValue(SPEC);
                Preconditions.checkNotNull(specName, SPEC);
                final String implName = child.getAttributeValue(IMPL);
                Preconditions.checkNotNull(implName, IMPL);
                
                final Class<Object> spec;
                final Class<? extends Object> impl;

                try {
                    @SuppressWarnings("unchecked")
                    final Class<Object> specClass = Class.class.cast(Class.forName(specName));
                    spec = specClass;
                    impl = Class.forName(implName);
                } catch (ClassNotFoundException e) {
                    throw new IllegalArgumentException(e);
                }

                final Key<Object> key;
                final String name = child.getAttributeValue(NAME);
                
                if (name == null) {
                    log.debug("No name given, binding {} to {}", spec, impl);
                    key = Key.get(spec);
                } else {
                    log.debug("Name given, binding {} to {} using name '{}'", new Object[] {
                        spec, impl, name
                    });
                    key = Key.get(spec, Names.named(name));
                }
                
                configuration.put(key, child);
                binder.bind(key).to(impl).in(Scopes.SINGLETON);
            }
            
        }
        
    }
    
    private void configure() {
        log.info("Configuring services");
        for (Configurable configurable : Iterables.filter(services.values(), Configurable.class)) {
            final Element element = configuration.get(services.inverse().get(configurable));
            log.info("Configuring {}", configurable);
            configurable.configure(element);
        }
    }
    
    private void initialize() {
        log.info("Initializing services");
        for (Initializable initializable : Iterables.filter(services.values(), Initializable.class)) {
            log.info("Initializing {}", initializable);
            initializable.initialize();
        }
    }
    
    private void start() {
        log.info("Starting services");
        for (Startable startable : Iterables.filter(services.values(), Startable.class)) {
            log.info("Starting {}", startable);
            startable.start();
        }
    }
    
    @Override
    public <T> T lookup(Class<T> spec) {
        Preconditions.checkNotNull(spec, "Spec");
        return injector.getInstance(spec);
    }
    
    @Override
    public <T> T lookup(Class<T> spec, String name) {
        Preconditions.checkNotNull(spec, "Spec");
        Preconditions.checkNotNull(name, "Name"); 
        return injector.getInstance(Key.get(spec, Names.named(name)));
    }
    
    @Override
    public void shutdown() {
        stop();
        dispose();
    }
    
    private void stop() {
        log.info("Stopping services");
        for (Startable startable : Iterables.filter(services.values(), Startable.class)) {
            log.info("Stopping {}", startable);
            try {
                startable.stop();
            } catch (LifecycleException e) {
                final String message = String.format("Unable to stop service %s", startable);
                log.warn(message, e);
            }
        }
    }
    
    private void dispose() {
        log.info("Disposing services");
        for (Disposable disposable : Iterables.filter(services.values(), Disposable.class)) {
            log.info("Disposing {}", disposable);
            try {
                disposable.dispose();
            } catch (LifecycleException e) {
                final String message = String.format("Unable to stop service %s", disposable);
                log.warn(message, e);
            }
        }
    }
    
}
