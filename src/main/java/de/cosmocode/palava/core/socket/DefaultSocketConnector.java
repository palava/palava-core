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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

import de.cosmocode.commons.State;
import de.cosmocode.palava.core.call.Call;
import de.cosmocode.palava.core.concurrent.ExecutorBuilder;
import de.cosmocode.palava.core.protocol.Response;

/**
 * Default implementation of the {@link SocketConnector} interface.
 *
 * @author Willi Schoenborn
 */
@Singleton
final class DefaultSocketConnector implements SocketConnector {
    
    private static final Logger log = LoggerFactory.getLogger(DefaultSocketConnector.class);
    
    private final int port;
    
    private final long socketTimeout;

    private final TimeUnit socketTimeoutUnit;
    
    private final long shutdownTimeout;

    private final TimeUnit shutdownTimeoutUnit;
    
    private ExecutorService service;
    
    private State state = State.NEW;
    
    @Inject
    public DefaultSocketConnector(
        ExecutorBuilder builder,
        @Named("core.socket.port") int port,
        @Named("core.socket.timeout") long socketTimeout,
        @Named("core.socket.timeoutUnit") TimeUnit socketTimeoutUnit,
        @Named("core.threadpool.minSize") int minPoolSize, 
        @Named("core.threadpool.maxSize") int maxPoolSize, 
        @Named("core.threadpool.keepAliveTime") long keepAliveTime,
        @Named("core.threadpool.keepAliveTimeUnit") TimeUnit keepAliveTimeUnit,
        @Named("core.threadpool.shutdownTimeout") long shutdownTimeout,
        @Named("core.threadpool.shutdownTimeoutUnit") TimeUnit shutdownTimeoutUnit) {
        
        Preconditions.checkNotNull(builder, "Builder");
        
        Preconditions.checkArgument(port >= 0, "Port must be positive, but was %s", port);
        this.port = port;
        
        this.socketTimeout = socketTimeout;
        this.socketTimeoutUnit = socketTimeoutUnit;
        this.shutdownTimeout = shutdownTimeout;
        this.shutdownTimeoutUnit = shutdownTimeoutUnit;
        
        builder.minSize(minPoolSize);
        builder.maxSize(maxPoolSize);
        builder.keepAlive(keepAliveTime, keepAliveTimeUnit);
        this.service = builder.build();
    }

    @Override
    public void run(final CallHandler handler) throws IOException {
        log.debug("Starting {}", this);
        state = State.STARTING;
        log.info("Creating socket on port {}", port);
        final ServerSocket socket = new ServerSocket(port);
        log.info("Setting socket timeout to {} {}", socketTimeout, socketTimeoutUnit.name().toLowerCase());
        final int timeout = (int) socketTimeoutUnit.toMillis(socketTimeout);
        socket.setSoTimeout(timeout);
        state = State.RUNNING;
        log.debug("{} is running", this);
        
        while (state == State.RUNNING) {
            
            final Socket client;
            
            try {
                // TODO fix
                if (this != null) return;
                client = socket.accept();
            } catch (SocketTimeoutException e) {
                continue;
            }
            
            service.execute(new Runnable() {
                
                @Override
                public void run() {
                    try {
                        final InputStream input = client.getInputStream();
                        final OutputStream output = client.getOutputStream();
                        
                        // TODO protocol parsing here?!
                        
                        final Call call = null;
                        final Response response = null;
                        handler.incomingCall(call, response);
                    } catch (IOException e) {
                        log.error("Error while reading from/writing to socket", e);
                        try {
                            // TODO add exception handling
                            client.getInputStream().close();
                            client.shutdownInput();
                            client.getOutputStream().close();
                            client.shutdownOutput();
                            client.close();
                        } catch (IOException inner) {
                            log.error("Failed to properly close socket", inner);
                        }
                    }
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
        } catch (InterruptedException e) {
            log.error("Interrupted while shutting down", e);
            state = State.FAILED;
        }
    }
    
}
