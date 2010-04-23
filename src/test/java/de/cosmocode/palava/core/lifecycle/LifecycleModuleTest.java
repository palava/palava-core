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

package de.cosmocode.palava.core.lifecycle;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.junit.Assert;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.ProvisionException;

import de.cosmocode.palava.core.DefaultRegistryModule;
import de.cosmocode.palava.core.Registry;
import de.cosmocode.palava.core.event.FrameworkStop;

/**
 * Tests all bindings provided by {@link LifecycleModule}.
 *
 * @author Willi Schoenborn
 */
public final class LifecycleModuleTest {

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
        
        private boolean stopped;
        
        @Override
        public void start() throws LifecycleException {
            
        }
        
        @Override
        public void stop() throws LifecycleException {
            stopped = true;
        }
        
        public boolean isStopped() {
            return stopped;
        }
        
    }

    /**
     * Helper class to test {@link Disposable}.
     *
     * @author Willi Schoenborn
     */
    private static class DisposableClass implements Disposable {
        
        private boolean disposed;
        
        @Override
        public void dispose() throws LifecycleException {
            disposed = true;
        }
        
        public boolean isDisposed() {
            return disposed;
        }
        
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

        final StoppableClass stoppable = injector.getInstance(StoppableClass.class);
        final Registry registry = injector.getInstance(Registry.class);
        Assert.assertFalse(stoppable.isStopped());
        registry.notify(FrameworkStop.class, FrameworkStop.PROCEDURE);
        Assert.assertTrue(stoppable.isStopped());
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

        final DisposableClass disposable = injector.getInstance(DisposableClass.class);
        final Registry registry = injector.getInstance(Registry.class);
        Assert.assertFalse(disposable.isDisposed());
        registry.notify(FrameworkStop.class, FrameworkStop.PROCEDURE);
        Assert.assertTrue(disposable.isDisposed());
    }

}
