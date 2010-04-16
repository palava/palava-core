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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package de.cosmocode.palava.core.lifecycle;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.junit.Assert;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.ProvisionException;

import de.cosmocode.palava.core.DefaultRegistryModule;
import de.cosmocode.palava.core.Registry;
import de.cosmocode.palava.core.event.PreFrameworkStop;

/**
 * Tests all bindings provided by {@link LifecycleModule}.
 *
 * @author Willi Schoenborn
 */
public final class LifecycleModuleTest {

    private static boolean stopped;
    private static boolean disposed;
    
    /**
     * Custom exception to test deep nested method invocation.
     *
     * @author Willi Schoenborn
     */
    private static class SuccessException extends RuntimeException {

        private static final long serialVersionUID = 1L;
        
    }
    
    /**
     * Helper class to test {@link Initializable}.
     *
     * @author Willi Schoenborn
     */
    private static class InitializableClass implements Initializable {
        
        @Override
        public void initialize() throws LifecycleException {
            throw new SuccessException();
        }
        
    }

    /**
     * Helper class to test {@link AutoStartable}.
     *
     * @author Willi Schoenborn
     */
    private static class AutoStartableClass implements AutoStartable {
        
        @Override
        public void start() throws LifecycleException {
            throw new SuccessException();
        }
        
        @Override
        public void stop() throws LifecycleException {
            throw new UnsupportedOperationException();
        }
        
    }

    /**
     * Helper class to test {@link Startable}.
     *
     * @author Willi Schoenborn
     */
    private static class StoppableClass implements Startable {
        
        @Override
        public void start() throws LifecycleException {
            
        }
        
        @Override
        public void stop() throws LifecycleException {
            LifecycleModuleTest.stopped = true;
        }
        
    }

    /**
     * Helper class to test {@link Disposable}.
     *
     * @author Willi Schoenborn
     */
    private static class DisposableClass implements Disposable {
        
        @Override
        public void dispose() throws LifecycleException {
            LifecycleModuleTest.disposed = true;
        }
        
    }
    
    /**
     * Runs before each test.
     */
    public void before() {
        stopped = false;
        disposed = false;
    }
    
    /**
     * Tests whether {@link Initializable}s get initialized.
     */
    @Test
    public void initialize() {
        final Injector injector = Guice.createInjector(
            new LifecycleModule(),
            new DefaultRegistryModule()
        );
        
        try {
            injector.getInstance(InitializableClass.class);
        } catch (ProvisionException e) {
            Assert.assertTrue(ExceptionUtils.getCause(e) instanceof SuccessException);
            return;
        }
        
        Assert.fail();
    }
    
    /**
     * Tests whether {@link AutoStartable}s get started.
     */
    @Test
    public void autostart() {
        final Injector injector = Guice.createInjector(
            new LifecycleModule(),
            new DefaultRegistryModule()
        );
        
        try {
            injector.getInstance(AutoStartableClass.class);
        } catch (ProvisionException e) {
            Assert.assertTrue(ExceptionUtils.getCause(e) instanceof SuccessException);
            return;
        }
        
        Assert.fail();
    }

    /**
     * Tests whether {@link Startable}s get stopped.
     */
    @Test
    public void stop() {
        final Injector injector = Guice.createInjector(
            new LifecycleModule(),
            new DefaultRegistryModule()
        );

        injector.getInstance(StoppableClass.class);
        final Registry registry = injector.getInstance(Registry.class);
        Assert.assertFalse(stopped);
        registry.notify(PreFrameworkStop.class, PreFrameworkStop.PROCEDURE);
        Assert.assertTrue(stopped);
    }
    
    /**
     * Tests whether {@link Disposable}s get disposed.
     */
    @Test
    public void dispose() {
        final Injector injector = Guice.createInjector(
            new LifecycleModule(),
            new DefaultRegistryModule()
        );

        injector.getInstance(DisposableClass.class);
        final Registry registry = injector.getInstance(Registry.class);
        Assert.assertFalse(disposed);
        registry.notify(PreFrameworkStop.class, PreFrameworkStop.PROCEDURE);
        Assert.assertTrue(disposed);
    }

}
