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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.io.Closeables;
import com.google.common.io.Resources;
import com.google.inject.ConfigurationException;
import com.google.inject.Module;
import com.google.inject.ProvisionException;
import com.google.inject.Stage;

import de.cosmocode.palava.core.inject.Settings;

/**
 * Static factory class for framework instances.
 *
 * @since 2.0
 * @author Willi Schoenborn
 */
public final class Palava {

    private static final Logger LOG = LoggerFactory.getLogger(Palava.class);
    
    private static final String RESOURCE_NAME = "application.properties";
    
    private Palava() {
        
    }

    /**
     * Creates a new {@link Framework} using properties provided as a classpath
     * resource.
     * <p>
     *   This method assumes there is a classpath resource named "application.properties".
     * </p>
     * <p>
     *   The loaded properties will be bound using the {@link Settings} annotation.
     * </p>
     * 
     * @since 2.4
     * @return a new configured {@link Framework} instance
     * @throws IllegalArgumentException if the application.properties resource does not exist
     * @throws ConfigurationException if guice configuration failed
     * @throws ProvisionException if providing an instance during creation failed
     */
    public static Framework newFramework() {
        return newFramework(Resources.getResource(RESOURCE_NAME));
    }
    
    /**
     * Creates a new {@link Framework} using the specified module and properties
     * provided as a classpath resource.
     * <p>
     *   This method assumes there is a classpath resource named "application.properties".
     * </p>
     * <p>
     *   The loaded properties will be bound using the {@link Settings} annotation.
     * </p>
     * 
     * @since 2.4
     * @param module the application main module
     * @return a new configured {@link Framework} instance
     * @throws NullPointerException if module is null
     * @throws IllegalArgumentException if the application.properties resource does not exist
     * @throws ConfigurationException if guice configuration failed
     * @throws ProvisionException if providing an instance during creation failed
     */
    public static Framework newFramework(Module module) {
        Preconditions.checkNotNull(module, "Module");
        return newFramework(module, Resources.getResource(RESOURCE_NAME));
    }
    
    /**
     * Creates a new {@link Framework} using a properties file.
     * <p>
     *   The loaded properties will be bound using the {@link Settings} annotation.
     * </p>
     * 
     * @since 2.4
     * @param file the file pointing to the properties file
     * @return a new configured {@link Framework} instance
     * @throws NullPointerException if file is null
     * @throws IllegalArgumentException if the file does not exist
     * @throws ConfigurationException if guice configuration failed
     * @throws ProvisionException if providing an instance during creation failed
     */
    public static Framework newFramework(File file) {
        Preconditions.checkNotNull(file, "File");
        Preconditions.checkArgument(file.exists(), "%s does not exist", file);
        try {
            return newFramework(file.toURI().toURL());
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(e);
        } 
    }
    
    /**
     * Creates a new {@link Framework} using a properties url.
     * <p>
     *   The loaded properties will be bound using the {@link Settings} annotation.
     * </p>
     * 
     * @since 2.4
     * @param url the url pointing to the properties file
     * @return a new configured {@link Framework} instance
     * @throws NullPointerException if url is null
     * @throws IllegalArgumentException if reading from the specified url failed
     * @throws ConfigurationException if guice configuration failed
     * @throws ProvisionException if providing an instance during creation failed
     */
    public static Framework newFramework(URL url) {
        Preconditions.checkNotNull(url, "URL");
        final InputStream stream;
        
        try {
            stream = url.openStream();
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
        
        try {
            final Properties properties = new Properties();
            properties.load(stream);
            return newFramework(properties);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        } finally {
            Closeables.closeQuietly(stream);
        }
    }
    
    /**
     * Creates a new {@link Framework} using the specified module and
     * a properties file.
     * <p>
     *   The loaded properties will be bound using the {@link Settings} annotation.
     * </p>
     * 
     * @since 2.4
     * @param module the application main module
     * @param file the file pointing to the properties file
     * @return a new configured {@link Framework} instance
     * @throws NullPointerException if module or file is null
     * @throws IllegalArgumentException if the file does not exist
     * @throws ConfigurationException if guice configuration failed
     * @throws ProvisionException if providing an instance during creation failed
     */
    public static Framework newFramework(Module module, File file) {
        Preconditions.checkNotNull(module, "Module");
        Preconditions.checkNotNull(file, "File");
        Preconditions.checkArgument(file.exists(), "%s does not exist", file);
        try {
            return newFramework(module, file.toURI().toURL());
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(e);
        } 
    }
    
    /**
     * Creates a new {@link Framework} using the specified module and
     * a properties url.
     * <p>
     *   The loaded properties will be bound using the {@link Settings} annotation.
     * </p>
     * 
     * @since 2.4
     * @param module the application main module
     * @param url the url pointing to the properties file
     * @return a new configured {@link Framework} instance
     * @throws NullPointerException if module or url is null
     * @throws IllegalArgumentException if reading from the specified url failed
     * @throws ConfigurationException if guice configuration failed
     * @throws ProvisionException if providing an instance during creation failed
     */
    public static Framework newFramework(Module module, URL url) {
        Preconditions.checkNotNull(module, "Module");
        Preconditions.checkNotNull(url, "URL");
        final InputStream stream;
        
        try {
            stream = url.openStream();
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
        
        try {
            final Properties properties = new Properties();
            properties.load(stream);
            return newFramework(module, properties);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        } finally {
            Closeables.closeQuietly(stream);
        }
    }
    
    /**
     * Creates a new {@link Framework} using the specified properties.
     * <p>
     *   This method assumes the specified properties contain the fully
     *   qualified class name of the main module under the default configuration
     *   key {@link CoreConfig#APPLICATION}. The class will be loaded vi reflection.
     * </p>
     * <p>
     *   The specified properties will be bound using the {@link Settings} annotation.
     * </p>
     * 
     * @since 2.3
     * @param properties the settings
     * @return a new configured {@link Framework} instance
     * @throws NullPointerException if properties is null or properties do not contain {@link CoreConfig#APPLICATION}
     * @throws IllegalArgumentException if the specified class does not exist or no instance could be created
     * @throws ClassCastException if the addressed main module class is no subclass of {@link Module}
     * @throws ConfigurationException if guice configuration failed
     * @throws ProvisionException if providing an instance during creation failed
     */
    public static Framework newFramework(Properties properties) {
        Preconditions.checkNotNull(properties, "Properties");

        final String className = properties.getProperty(CoreConfig.APPLICATION);
        Preconditions.checkNotNull(className, "Missing %s", CoreConfig.APPLICATION);
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
     * Creates a new {@link Framework} using the specified properties.
     * <p>
     *   This method creates a new instance of the given module class using
     *   {@link Class#newInstance()}. As a consequence this class must provide
     *   a public no argument constructor.
     * </p>
     * <p>
     *   The specified properties will be bound using the {@link Settings} annotation.
     * </p>
     * 
     * @since 2.3
     * @param moduleClass the class literal of the main module
     * @param properties the application properties
     * @return a new configured {@link Framework} instance
     * @throws NullPointerException if mainModuleClass or properties is null
     * @throws IllegalArgumentException if creating instance of mainModuleClass failed
     * @throws ConfigurationException if guice configuration failed
     * @throws ProvisionException if providing an instance during creation failed
     */
    public static Framework newFramework(Class<? extends Module> moduleClass, Properties properties) {
        Preconditions.checkNotNull(moduleClass, "ModuleClass");
        Preconditions.checkNotNull(properties, "Properties");
        
        final Module module;

        try {
            module = moduleClass.newInstance();
        } catch (InstantiationException e) {
            throw new IllegalArgumentException(e);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(e);
        }
        
        return newFramework(module, properties);
    }
    
    /**
     * Creates a {@link Framework} which loads the given {@link Module}.
     * <p>
     *   The specified properties will be bound using the {@link Settings} annotation.
     * </p>
     * 
     * @since 2.3
     * @param module the application main module
     * @param properties the application properties
     * @return a new configured {@link Framework} instance
     * @throws NullPointerException if mainModule or properties is null
     * @throws ConfigurationException if guice configuration failed
     * @throws ProvisionException if providing an instance during creation failed
     */
    public static Framework newFramework(Module module, Properties properties) {
        Preconditions.checkNotNull(module, "Module");
        Preconditions.checkNotNull(properties, "Properties");
        final String stageName = properties.getProperty(CoreConfig.STAGE);
        final Stage stage = StringUtils.isBlank(stageName) ? Stage.PRODUCTION : Stage.valueOf(stageName);
        return newFramework(module, stage, properties); 
    }
    
    /**
     * Creates a {@link Framework} which loads the given {@link Module} using the specified
     * guice {@link Stage}.
     * <p>
     *   The specified properties will be bound using the {@link Settings} annotation.
     * </p>
     * 
     * @since 2.3
     * @param module the application main module
     * @param stage the desired injector stage
     * @param properties the application properties
     * @return a new configured {@link Framework} instance
     * @throws NullPointerException if mainModule, stage or properties is null
     * @throws ConfigurationException if guice configuration failed
     * @throws ProvisionException if providing an instance during creation failed
     */
    public static Framework newFramework(Module module, Stage stage, Properties properties) {
        Preconditions.checkNotNull(module, "Module");
        Preconditions.checkNotNull(stage, "Stage");
        Preconditions.checkNotNull(properties, "Properties");
        return new BootstrapFramework(module, stage, properties);
    }
    
}
