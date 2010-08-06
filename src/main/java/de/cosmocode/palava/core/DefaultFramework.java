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

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;

import de.cosmocode.palava.core.event.FrameworkStart;
import de.cosmocode.palava.core.event.FrameworkStop;
import de.cosmocode.palava.core.event.PostFrameworkStart;
import de.cosmocode.palava.core.event.PostFrameworkStop;
import de.cosmocode.palava.core.event.PreFrameworkStart;
import de.cosmocode.palava.core.event.PreFrameworkStop;

/**
 * Implementation of the {@link Framework} interface which requires
 * a running guice environment.
 *
 * @since 2.3
 * @author Willi Schoenborn
 */
final class DefaultFramework extends AbstractFramework {

    private final Injector injector;
    private final Registry registry;
    
    @Inject
    public DefaultFramework(Injector injector, Registry registry) {
        this.injector = Preconditions.checkNotNull(injector, "Injector");
        this.registry = Preconditions.checkNotNull(registry, "Registry");
    }

    @Override
    protected void doStart() {
        Palava.addFramework(this);
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
        Palava.removeFramework(this);
    }

}
