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

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.inject.Inject;

import de.cosmocode.palava.core.Registry;
import de.cosmocode.palava.core.event.PreFrameworkStop;

/**
 * A listener for the {@link PreFrameworkStop} event which stops all
 * {@link Startable} and disposes all {@link Disposable} services.
 *
 * @author Willi Schoenborn
 */
final class DisposableListener implements PreFrameworkStop {

    private static final Logger LOG = LoggerFactory.getLogger(DisposableListener.class);

    private final List<Object> services;
    
    @Inject
    public DisposableListener(@LifecycleServices List<Object> services, Registry registry) {
        this.services = Preconditions.checkNotNull(services, "Services");
        Preconditions.checkNotNull(registry, "Registry").register(PreFrameworkStop.class, this);
    }

    private <T> Iterable<T> filterAndReverse(Class<T> type) {
        return Iterables.reverse(Lists.newArrayList(Iterables.filter(services, type)));
    }

    private void stopServices() {
        LOG.info("Stopping services");
        for (Startable startable : filterAndReverse(Startable.class)) {
            LOG.info("Stopping {}", startable);
            try {
                startable.stop();
            /* CHECKSTYLE:OFF */
            } catch (RuntimeException e) {
            /* CHECKSTYLE:ON */
                LOG.warn(String.format("Unable to stop service %s", startable), e);
            }
        }
    }

    private void disposeServices() {
        LOG.info("Disposing services");
        for (Disposable disposable : filterAndReverse(Disposable.class)) {
            LOG.info("Disposing {}", disposable);
            try {
                disposable.dispose();
            /* CHECKSTYLE:OFF */
            } catch (RuntimeException e) {
            /* CHECKSTYLE:ON */
                LOG.warn(String.format("Unable to dispose service %s", disposable), e);
            }
        }
    }

    @Override
    public void eventPreFrameworkStop() {
        stopServices();
        disposeServices();
    }

}
