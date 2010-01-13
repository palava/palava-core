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
import java.util.Set;

import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.inject.Binder;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.Singleton;
import com.google.inject.internal.Sets;
import com.google.inject.name.Named;
import com.google.inject.name.Names;

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
    
    private final Set<Object> services = Sets.newHashSet();
    
    @Inject
    public DefaultServiceManager(Injector injector, @Named("ServiceModule") Element configuration) {
        this.injector = Preconditions.checkNotNull(injector, "Injector");
        
        Preconditions.checkNotNull(configuration, "Configuration");
        final String path = configuration.getChildText(FILE);
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
        
        injector.createChildInjector(new IntegratedServiceModule());
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

                final String name = child.getAttributeValue(NAME);
                
                if (name == null) {
                    log.debug("No name given, binding {} to {}", spec, impl);
                    binder.bind(spec).to(impl);
                } else {
                    log.debug("Name given, binding {} to {} using name '{}'", new Object[] {
                        spec, impl, name
                    });
                    binder.bind(spec).annotatedWith(Names.named(name)).to(impl);
                }
            }
            
        }
        
    }
    
    @Override
    public <T> T lookup(Class<T> spec) {
        return injector.getInstance(spec);
    }
    
    @Override
    public <T> T lookup(Class<T> spec, String name) {
        return injector.getInstance(Key.get(spec, Names.named(name)));
    }
    
    @Override
    public void shutdown() {
        
    }
    
}
