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

package de.cosmocode.palava.core;

import java.util.Properties;

import org.apache.commons.lang.StringUtils;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.Stage;

import de.cosmocode.commons.State;
import de.cosmocode.palava.core.event.PostFrameworkStart;
import de.cosmocode.palava.core.event.PreFrameworkStop;
import de.cosmocode.palava.core.inject.SettingsModule;

/**
 * An implementation of the {@link Framework} which bootstraps guice
 * using {@link Guice#createInjector(Stage, Module...)}.
 * This implementations is used for running palava standalone or
 * in embedded mode in a normal java se environment without guice support.
 *
 * @author Willi Schoenborn
 */
final class BootstrapFramework extends AbstractFramework {

    private final Injector injector;
    private final Registry registry;
    
    public BootstrapFramework(Module module, Properties properties) {
        final String stageName = properties.getProperty(CoreConfig.STAGE);
        final Stage stage = StringUtils.isBlank(stageName) ? Stage.PRODUCTION : Stage.valueOf(stageName);
        
        try {
            injector = Guice.createInjector(stage, module, new SettingsModule(properties));
            registry = getInstance(Registry.class);
        /* CHECKSTYLE:OFF */
        } catch (RuntimeException e) {
        /* CHECKSTYLE:ON */
            setState(State.FAILED);
            throw e;
        }
    }

    @Override
    protected void doStart() {
        registry.notifySilent(PostFrameworkStart.class, PostFrameworkStart.PROCEDURE);
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
        registry.notifySilent(PreFrameworkStop.class, PreFrameworkStop.PROCEDURE);
    }

}
