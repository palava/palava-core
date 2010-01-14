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

package de.cosmocode.palava.core.socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import org.jdom.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import de.cosmocode.commons.State;
import de.cosmocode.palava.core.concurrent.ExecutorBuilder;
import de.cosmocode.palava.core.protocol.Call;
import de.cosmocode.palava.core.protocol.Response;
import de.cosmocode.palava.core.service.lifecycle.Config;

/**
 * Default implementation of the {@link SocketConnector} interface.
 *
 * @author Willi Schoenborn
 */
@Singleton
final class DefaultSocketConnector implements SocketConnector {
    
    private static final Logger log = LoggerFactory.getLogger(DefaultSocketConnector.class);

    private State state = State.NEW;
    
    private final ExecutorService service;
    
    private final int port;
    
    private final int socketTimeout;
    
    private final long shutdownTimeout;
    
    private final TimeUnit shutdownTimeoutUnit;
    
    @Inject
    public DefaultSocketConnector(ExecutorBuilder builder, @Config(SocketModule.class) Element config) {
        Preconditions.checkNotNull(builder, "Builder");
    
        this.port = Integer.parseInt(Preconditions.checkNotNull(
            config.getChildText("port"), "port"));
        
        final long timeout = Integer.parseInt(Preconditions.checkNotNull(
            config.getChildText("socketTimeout"), "socketTimeout"));
        final TimeUnit socketTimeoutUnit = TimeUnit.valueOf(Preconditions.checkNotNull(
            config.getChildText("socketTimeoutUnit"), "socketTimeoutUnit").toUpperCase());
        
        this.socketTimeout = (int) socketTimeoutUnit.toMillis(timeout);
        
        final int minPoolSize = Integer.parseInt(Preconditions.checkNotNull(
            config.getChildText("minPoolSize"), "minPoolSize"));
        final int maxPoolSize = Integer.parseInt(Preconditions.checkNotNull(
            config.getChildText("maxPoolSize"), "maxPoolSize"));
        final long keepAliveTime = Long.parseLong(Preconditions.checkNotNull(
            config.getChildText("keepAliveTime"), "keepAliveTime"));
        final TimeUnit keepAliveTimeUnit = TimeUnit.valueOf(Preconditions.checkNotNull(
            config.getChildText("keepAliveTimeUnit"), "keepAliveTimeUnit").toUpperCase());
        
        this.shutdownTimeout = Long.parseLong(Preconditions.checkNotNull(
            config.getChildText("shutdownTimeout"), "shutdownTimeout"));
        this.shutdownTimeoutUnit = TimeUnit.valueOf(Preconditions.checkNotNull(
            config.getChildText("shutdownTimeoutUnit"), "shutdownTimeoutUnit").toUpperCase());
        
        builder.minSize(minPoolSize);
        builder.maxSize(maxPoolSize);
        builder.keepAlive(keepAliveTime, keepAliveTimeUnit);
        this.service = builder.build(); 
        
    }
    
    @Override
    public void run(final RequestCallback callback) throws IOException {
        log.debug("Starting {}", this);
        state = State.STARTING;
        log.info("Creating socket on port {}", port);
        final ServerSocket socket = new ServerSocket(port);
        log.info("Setting socket timeout to {}", socketTimeout);
        socket.setSoTimeout(socketTimeout);
        state = State.RUNNING;
        log.debug("{} is running", this);
        
        while (state == State.RUNNING) {
            
            final Socket client;
            
            try {
                if (this != null) return;
                client = socket.accept();
            } catch (SocketTimeoutException e) {
                continue;
            }
            
            service.execute(new Runnable() {
                
                @Override
                public void run() {
                    // TODO request scope enter
                    try {
                        final InputStream input = client.getInputStream();
                        final OutputStream output = client.getOutputStream();
                        
                        // TODO protocol parsing here?!
                        
                        final Call request = null;
                        final Response response = null;
                        callback.incomingRequest(request, response);
                    } catch (IOException e) {
                        log.error("Error while reading from/writing to socket", e);
                        try {
                            client.getInputStream().close();
                            client.shutdownInput();
                            client.getOutputStream().close();
                            client.shutdownOutput();
                            client.close();
                        } catch (IOException inner) {
                            log.error("Failed to properly close socket", inner);
                        }
                    }
                    // TODO request scope exit
                }
                
            });
            
        }
    }

    @Override
    public void stop() {
        log.debug("Stopping {}", this);
        state = State.STOPPING;
        service.shutdown();
        
        try {
            if (service.awaitTermination(shutdownTimeout, shutdownTimeoutUnit)) {
                log.debug("Shutdown of {} successful", service);
            } else {
                log.warn("Forced shutdown of {}", service);
            }
            state = State.TERMINATED;
        } catch (InterruptedException ignored) {
            log.error("Interrupted while shutting down");
            state = State.FAILED;
        }
    }
    
}
