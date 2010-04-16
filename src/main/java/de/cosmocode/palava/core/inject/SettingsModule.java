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

package de.cosmocode.palava.core.inject;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.name.Names;


/**
 * A {@link Module} which binds the given {@link Properties}
 * as {@link Settings}.
 *
 * @author Willi Schoenborn
 */
public final class SettingsModule implements Module {

    private static final Logger LOG = LoggerFactory.getLogger(SettingsModule.class);

    private final Properties properties;

    public SettingsModule(Properties properties) {
        this.properties = Preconditions.checkNotNull(properties, "Properties");
    }

    @Override
    public void configure(Binder binder) {
        Names.bindProperties(binder, properties);
        LOG.debug("Binding properties {} as settings", properties);
        binder.bind(Properties.class).annotatedWith(Settings.class).toInstance(properties);
    }

}
