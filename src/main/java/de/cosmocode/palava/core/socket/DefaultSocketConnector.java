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
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

import de.cosmocode.commons.State;
import de.cosmocode.palava.core.concurrent.ExecutorBuilder;

/**
 * Default implementation of the {@link SocketConnector} interface.
 *
 * @author Willi Schoenborn
 */
@Singleton
final class DefaultSocketConnector implements SocketConnector, ThreadFactory, UncaughtExceptionHandler {
    
    private static final Logger log = LoggerFactory.getLogger(DefaultSocketConnector.class);
    
    private final Communicator communicator;
    
    private final int port;
    
    private final long socketTimeout;

    private final TimeUnit socketTimeoutUnit;
    
    private final long shutdownTimeout;

    private final TimeUnit shutdownTimeoutUnit;
    
    private final ExecutorService service;
    
    private State state = State.NEW;
    
    @Inject
    public DefaultSocketConnector(
        Communicator communicator,
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
        
        this.communicator = Preconditions.checkNotNull(communicator, "Communicator");
        
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
        builder.threadFactory(this);
        this.service = builder.build();
    }

    @Override
    public Thread newThread(Runnable r) {
        final Thread thread = new Thread(r);
        thread.setUncaughtExceptionHandler(this);
        return thread;
    }
    
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        log.error("Uncaught exception during connection", e);
    }
    
    @Override
    public void run() throws IOException {
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
                client = socket.accept();
            } catch (SocketTimeoutException e) {
                continue;
            }

            service.execute(new Runnable() {
                
                @Override
                public void run() {
                    final InputStream input;
                    final OutputStream output;
                    
                    try {
                        input = client.getInputStream();
                        output = client.getOutputStream();
                    } catch (IOException e) {
                        throw new IllegalStateException(e);
                    }

                    try {
                        communicator.communicate(input, output);
                    } finally {
                        try {
//                            client.getInputStream().close();
//                            client.shutdownInput();
//                            client.getOutputStream().close();
//                            client.shutdownOutput();
                            client.close();
                        } catch (IOException e) {
                            log.warn("Closing socket failed", e);
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
