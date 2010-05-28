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

import java.util.Properties;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.Stage;

import de.cosmocode.palava.core.event.FrameworkStart;
import de.cosmocode.palava.core.event.FrameworkStop;
import de.cosmocode.palava.core.event.PostFrameworkStart;
import de.cosmocode.palava.core.event.PostFrameworkStop;
import de.cosmocode.palava.core.event.PreFrameworkStart;
import de.cosmocode.palava.core.event.PreFrameworkStop;
import de.cosmocode.palava.core.inject.SettingsModule;

/**
 * An implementation of the {@link Framework} which bootstraps guice
 * using {@link Guice#createInjector(Stage, Module...)}.
 * This implementations is used for running palava standalone or
 * in embedded mode in a normal java se environment without guice support.
 *
 * @since 2.3
 * @author Willi Schoenborn
 */
final class BootstrapFramework extends AbstractFramework {

    private final Injector injector;
    private final Registry registry;
    
    public BootstrapFramework(Module module, Stage stage, Properties properties) {
        try {
            injector = Guice.createInjector(stage, 
                module,
                new FrameworkModule(this),
                new SettingsModule(properties)
            );
            registry = getInstance(Registry.class);
        /* CHECKSTYLE:OFF */
        } catch (RuntimeException e) {
        /* CHECKSTYLE:ON */
            fail();
            throw e;
        }
    }
    
    @Override
    protected void doStart() {
        registry.notify(PreFrameworkStart.class, PreFrameworkStart.PROCEDURE);
        registry.notify(FrameworkStart.class, FrameworkStart.PROCEDURE);
        registry.notifySilently(PostFrameworkStart.class, PostFrameworkStart.PROCEDURE);
    }
    
    @Override
    public <T> T getInstance(Class<T> type) {
        return injector.getInstance(type);
    }
    
    @Override
    public <T> T getInstance(Key<T> key) {
        return injector.getInstance(key);
    }

    @Override
    protected void doStop() {
        registry.notifySilently(PreFrameworkStop.class, PreFrameworkStop.PROCEDURE);
        registry.notifySilently(FrameworkStop.class, FrameworkStop.PROCEDURE);
        registry.notifySilently(PostFrameworkStop.class, PostFrameworkStop.PROCEDURE);
    }

}
