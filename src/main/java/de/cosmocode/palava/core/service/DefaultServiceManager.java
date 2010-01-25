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

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Singleton;
import com.google.inject.name.Names;

import de.cosmocode.palava.core.server.lifecycle.PostServerStopListener;
import de.cosmocode.palava.core.service.lifecycle.Disposable;
import de.cosmocode.palava.core.service.lifecycle.LifecycleException;
import de.cosmocode.palava.core.service.lifecycle.Startable;

/**
 * Default implementation of the {@link ServiceManager} interface.
 *
 * @author Willi Schoenborn
 */
@Singleton
final class DefaultServiceManager implements ServiceManager, PostServerStopListener {
    
    private static final Logger log = LoggerFactory.getLogger(DefaultServiceManager.class);
    
    private final Injector injector;

    private final Set<Service> services;
    
    @Inject
    public DefaultServiceManager(Injector injector, Set<Service> services) {
        this.injector = Preconditions.checkNotNull(injector, "Injector");
        this.services = Preconditions.checkNotNull(services, "Services");
        log.info("Found {} services", services.size());
        for (Service service : services) {
            log.debug("Created {}", service);
        }
    }
    
//    @Override
//    public void start() {
//        initializeServices();
//        startServices();
//    }
//    
//    private void initializeServices() {
//        log.info("Initializing services");
//        for (Initializable initializable : Iterables.filter(services, Initializable.class)) {
//            log.info("Initializing {}", initializable);
//            initializable.initialize();
//        }
//    }
//    
//    private void startServices() {
//        log.info("Starting services");
//        for (Startable startable : Iterables.filter(services, Startable.class)) {
//            log.info("Starting {}", startable);
//            startable.start();
//        }
//    }
    
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
    public void afterStop() {
        stopServices();
        disposeServices();
    }
    
    private void stopServices() {
        log.info("Stopping services");
        for (Startable startable : Iterables.filter(services, Startable.class)) {
            log.info("Stopping {}", startable);
            try {
                startable.stop();
            } catch (LifecycleException e) {
                final String message = String.format("Unable to stop service %s", startable);
                log.warn(message, e);
            }
        }
    }
    
    private void disposeServices() {
        log.info("Disposing services");
        for (Disposable disposable : Iterables.filter(services, Disposable.class)) {
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
