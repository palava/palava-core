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

package de.cosmocode.palava.core.server;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;

import de.cosmocode.commons.State;
import de.cosmocode.palava.core.lifecycle.Startable;
import de.cosmocode.palava.core.socket.SocketConnector;

/**
 * Default implementation of the {@link Server} interface.
 *
 * @author Willi Schoenborn
 */
@Singleton
final class DefaultServer implements Server, ServiceManager, Startable, Runnable {

    private static final Logger log = LoggerFactory.getLogger(DefaultServer.class);

    private final Injector injector;
    
    private final SocketConnector socketConnector;
    
    private State state = State.NEW;
    
    @Inject
    public DefaultServer(Injector injector, SocketConnector connector) {
        this.injector = Preconditions.checkNotNull(injector, "Injector");
        this.socketConnector = Preconditions.checkNotNull(connector, "SocketConnector");
    }

    @Override
    public void start() {
        log.info("Starting server");
        final Thread thread = new Thread(this);
        thread.setDaemon(false);
        thread.start();
    }
    
    @Override
    public void run() {
        state = State.STARTING;
        
        try {
            state = State.RUNNING;
            socketConnector.run();
            state = State.TERMINATED;
        } catch (IOException e) {
            log.error("Socket error", e);
            state = State.FAILED;
        } 
    }
    
    @Override
    public ServiceManager getServiceManager() {
        return this;
    }
    
    @Override
    public <T> T lookup(Class<T> spec) {
        Preconditions.checkNotNull(spec, "Spec");
        return injector.getInstance(spec);
    }
    
    @Override
    public State currentState() {
        return state;
    }
    
    @Override
    public boolean isRunning() {
        return state == State.RUNNING;
    }
    
    @Override
    public void stop() {
        if (state == State.RUNNING) {
            log.debug("Stopping server");
            state = State.STOPPING;
            socketConnector.stop();
        } else {
            log.info("Can't stop, server is {}", state.name().toLowerCase());
        }
    }
    
}
