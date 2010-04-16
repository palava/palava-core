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
