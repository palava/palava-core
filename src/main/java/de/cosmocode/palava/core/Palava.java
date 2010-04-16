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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package de.cosmocode.palava.core;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.inject.ConfigurationException;
import com.google.inject.Module;
import com.google.inject.ProvisionException;

import de.cosmocode.palava.core.inject.Settings;

/**
 * Static factory class for framework instances.
 *
 * @author Willi Schoenborn
 */
public final class Palava {

    private static final Logger LOG = LoggerFactory.getLogger(Palava.class);
    
    private Palava() {
        
    }

    /**
     * Constructs a new palava framework using the given properties.
     * 
     * @deprecated use {@link Palava#newFramework(Properties)} instead
     * @param properties the application properties
     * @return a configured {@link Framework} instance
     */
    @Deprecated
    public static Framework createFramework(Properties properties) {
        return newFramework(properties);
    }
    
    /**
     * Constructs a new {@link Framework} using the specified properties.
     * <p>
     *   This method assumes the specified properties contain the fully
     *   qualified class name of the main module under the default configuration
     *   key {@link CoreConfig#APPLICATION}.
     * </p>
     * <p>
     *   The specified properties will be bound using the {@link Settings} annotation.
     * </p>
     * 
     * @param properties the settings
     * @return a new configured {@link Framework} instance
     * @throws NullPointerException if properties is null
     * @throws IllegalArgumentException if the specified class does not exist
     * @throws ClassCastException if the addressed main module class is no subclass of {@link Module}
     * @throws ConfigurationException if guice configuration failed
     * @throws ProvisionException if providing an instance during creation failed
     */
    public static Framework newFramework(Properties properties) {
        Preconditions.checkNotNull(properties, "Properties");

        final String className = properties.getProperty(CoreConfig.APPLICATION);
        Preconditions.checkNotNull(className, CoreConfig.APPLICATION);
        final Class<? extends Module> mainModuleClass;

        try {
            mainModuleClass = Class.forName(className).asSubclass(Module.class);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
        
        LOG.debug("Creating new framework using {}", properties);
        return newFramework(mainModuleClass, properties);
    }
    
    /**
     * Constructs a new {@link Framework} using the specified properties.
     * <p>
     *   This method creates a new instance of the given module class using
     *   {@link Class#newInstance()}. As a consequence this class must provide
     *   a public no argument constructor.
     * </p>
     * <p>
     *   The specified properties will be bound using the {@link Settings} annotation.
     * </p>
     * 
     * @param mainModuleClass the class literal of the main module
     * @param properties the application properties
     * @return a new configured {@link Framework} instance
     * @throws IllegalArgumentException if creating instance of mainModuleClass failed
     * @throws ConfigurationException if guice configuration failed
     * @throws ProvisionException if providing an instance during creation failed
     */
    public static Framework newFramework(Class<? extends Module> mainModuleClass, Properties properties) {
        Preconditions.checkNotNull(mainModuleClass, "MainModuleClass");
        Preconditions.checkNotNull(properties, "Properties");
        
        final Module mainModule;

        try {
            mainModule = mainModuleClass.newInstance();
        } catch (InstantiationException e) {
            throw new IllegalArgumentException(e);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(e);
        }
        
        return newFramework(mainModule, properties);
    }
    
    /**
     * Creates a {@link Framework} which loads the given {@link Module}.
     * <p>
     *   The specified properties will be bound using the {@link Settings} annotation.
     * </p>
     * 
     * @param mainModule the application main module
     * @param properties the application properties
     * @return a new configured {@link Framework} instance
     * @throws ConfigurationException if guice configuration failed
     * @throws ProvisionException if providing an instance during creation failed
     */
    public static Framework newFramework(Module mainModule, Properties properties) {
        Preconditions.checkNotNull(mainModule, "MainModule");
        Preconditions.checkNotNull(properties, "Properties");
        return new BootstrapFramework(mainModule, properties);
    }
    
}
