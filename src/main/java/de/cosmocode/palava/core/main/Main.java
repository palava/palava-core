/**
 * palava - a java-php-bridge
 * Copyright (C) 2007  CosmoCode GmbH
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

package de.cosmocode.palava.core.main;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.inject.Binder;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.name.Names;

import de.cosmocode.palava.core.server.Server;

/**
 * Application entry point.
 *
 * @author Willi Schoenborn
 */
public final class Main {
    
    private static final Logger log = LoggerFactory.getLogger(Main.class);
    
    @Option(name = "-c",  required = true, aliases = "--config", usage = "Path to settings file")
    private File settings;
    
    private Main() {
        
    }
    
    private void run(String[] args) {
        final CmdLineParser parser = new CmdLineParser(this);
        
        try {
            parser.parseArgument(args);
        } catch (CmdLineException e) {
            parser.printUsage(System.err);
            throw new IllegalArgumentException(e);
        }
        
        Preconditions.checkNotNull(settings, "Settings file not set");
        Preconditions.checkState(settings.exists(), "Settings file does not exist");
        
        final Module module = configure();
        final Injector injector = Guice.createInjector(module);
        
        final Server server = injector.getInstance(Server.class);
        log.debug("Created server {}", server);
        
        log.info("Starting server");
        server.start();
        log.info("Server successfully stopped");
    }
    
    private Module configure() {
        final Module module;
        
        final Properties properties = new Properties();
        
        try {
            properties.load(new FileReader(settings));
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
        
        final String className = properties.getProperty("core.main.module");
        final Class<? extends Module> moduleClass;

        try {
            moduleClass = Class.forName(className).asSubclass(Module.class);
            module = moduleClass.newInstance();
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(e);
        } catch (InstantiationException e) {
            throw new IllegalArgumentException(e);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(e);
        }
        
        return new Module() {
            
            @Override
            public void configure(Binder binder) {
                Names.bindProperties(binder, properties);
                binder.bind(Map.class).annotatedWith(Settings.class).toInstance(properties);
                binder.install(module);
            }
            
        };
    }
    
    /**
     * Application entry point.
     * 
     * @param args command line arguments
     * @throws CmdLineException if command line parsing failed
     */
    public static void main(String[] args) throws CmdLineException {
        new Main().run(args);
    }

}
