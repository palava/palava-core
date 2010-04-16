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

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.cosmocode.collections.MultiProperties;
import de.cosmocode.commons.State;

/**
 * Application entry point.
 *
 * @author Willi Schoenborn
 * @author Tobias Sarnowski
 */
public final class Main {

    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    private final Options options = new Options();

    private final Framework framework;

    private Main(String[] args) {
        final CmdLineParser parser = new CmdLineParser(options);

        try {
            parser.parseArgument(args);
        } catch (CmdLineException e) {
            System.err.println("Usage:  java [options] configuration-files...");
            parser.printUsage(System.err);
            throw new IllegalArgumentException(e);
        }

        final Properties properties;
        
        try {
            properties = MultiProperties.load(options.getConfigs());
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
        
        try {
            framework = Palava.newFramework(properties);
        } finally {
            persistState();
        }
    }

    private void start() {
        try {
            framework.start();
            persistState();
        /* CHECKSTYLE:OFF */
        } catch (RuntimeException e) {
        /* CHECKSTYLE:ON */
            LOG.error("startup failed", e);
            stop();
            // TODO no rethrow?
        }
    }

    private void stop() {
        try {
            framework.stop();
        } finally {
            persistState();
        }
    }

    private void persistState() {
        if (options.getStateFile() == null) return;

        try {
            final State state = framework == null ? State.FAILED : framework.currentState();
            final Writer writer = new FileWriter(options.getStateFile());
            try {
                IOUtils.write(state.name() + "\n", writer);
            } finally {
                writer.close();
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("cannot persist state to file", e);
        }
    }

    private void waitIfNecessary() {
        if (options.isNoAutoShutdown()) {
            LOG.debug("Automatic shutdown disabled; running until someone else triggers the shutdown");

            synchronized (Thread.currentThread()) {
                try {
                    Thread.currentThread().wait();
                } catch (InterruptedException e) {
                    // it's ok, shut down
                    LOG.debug("main thread interrupted", e);
                }
            }
        }
    }
    
    /**
     * Application entry point.
     *
     * @param args command line arguments
     * @throws CmdLineException if command line parsing failed
     */
    public static void main(String[] args) throws CmdLineException {
        AsciiArt.print();

        final Main main;
        
        try {
            main = new Main(args);
        /* CHECKSTYLE:OFF */
        } catch (RuntimeException e) {
        /* CHECKSTYLE:ON */
            LOG.error("configuration error", e);
            throw e;
        }

        main.start();

        LOG.debug("Adding shutdown hook");
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

            @Override
            public void run() {
                main.stop();
            }

        }));

        main.waitIfNecessary();
    }

}
