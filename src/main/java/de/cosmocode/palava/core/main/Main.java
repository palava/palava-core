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
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Binder;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.internal.Sets;
import com.google.inject.name.Named;
import com.google.inject.name.Names;

import de.cosmocode.palava.core.server.Server;

/**
 * Application entry point.
 *
 * @author Willi Schoenborn
 */
public final class Main {
    
    private static final Logger log = LoggerFactory.getLogger(Main.class);
    
    private static final String NAME = "name";
    private static final String CONFIGURATION = "configuration";

    @Option(name = "-c",  required = true, aliases = "--config", usage = "Path to modules.xml")
    private File config;
    
    private Main() {
        
    }
    
    private void run(String[] args) throws CmdLineException {
        final CmdLineParser parser = new CmdLineParser(this);
        
        try {
            parser.parseArgument(args);
        } catch (CmdLineException e) {
            parser.printUsage(System.err);
            throw e;
        }
        
        final Collection<Module> modules = configure();
        log.info("Found {} core modules. Binding...", modules.size());
        final Injector injector = Guice.createInjector(modules);
        log.info("Binding complete");
        
        final Server server = injector.getInstance(Server.class);
        log.debug("Created server {}", server);
        
        log.info("Starting server");
        server.run();
        log.info("Server successfully stopped");
    }
    
    private Collection<Module> configure() {
        final Set<Module> modules = Sets.newHashSet();
        final Element root;
        
        try {
            root = new SAXBuilder().build(config).getRootElement();
        } catch (JDOMException e) {
            throw new IllegalArgumentException(e);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
        
        @SuppressWarnings("unchecked")
        final List<Element> children = root.getChildren();
        
        for (final Element child : children) {
            final String moduleName = child.getAttributeValue(NAME);
            log.info("Found module {}", moduleName);
            
            try {
                final Class<? extends Module> moduleClass = Class.forName(moduleName).asSubclass(Module.class);
                final Module module = moduleClass.newInstance();
                
                log.debug("Created new module instance: {}", module);
                
                final Element configuration = child.getChild(CONFIGURATION);
                
                if (configuration == null) {
                    log.debug("No configuration found for {}", module);
                    modules.add(module);
                } else {
                    log.debug("Found configuration for {}", module);
                    
                    final Named configurationName = Names.named(moduleClass.getSimpleName());
                    modules.add(new Module() {
                        
                        @Override
                        public void configure(Binder binder) {
                            binder.install(module);
                            binder.bind(Key.get(Element.class, configurationName)).toInstance(configuration);
                        }
                        
                    });
                }
            } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException(e);
            } catch (InstantiationException e) {
                throw new IllegalArgumentException(e);
            } catch (IllegalAccessException e) {
                throw new IllegalArgumentException(e);
            }
        }
        
        return modules;
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
