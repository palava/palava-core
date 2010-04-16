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

package de.cosmocode.palava.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.cosmocode.commons.State;

/**
 * Abstract base implementation of the {@link Framework} interface.
 *
 * @author Willi Schoenborn
 */
abstract class AbstractFramework implements Framework {

    private final Logger log = LoggerFactory.getLogger(getClass());
    
    private State state = State.NEW;
    
    @Override
    public final State currentState() {
        return state;
    };
    
    protected final void setState(State state) {
        this.state = state;
    }
    
    @Override
    public final boolean isRunning() {
        return state == State.RUNNING;
    }
    
    @Override
    public final void start() {
        state = State.STARTING;
        try {
            log.info("Starting framework");
            doStart();
            state = State.RUNNING;
            log.info("Framework is running");
        /* CHECKSTYLE:OFF */
        } catch (Exception e) {
        /* CHECKSTYLE:ON */
            state = State.FAILED;
            log.error("Starting framework failed", e);
        }
    }
    
    /**
     * Template method for {@link Framework#start()}.
     */
    protected abstract void doStart();
    
    @Override
    public final void stop() {
        final State oldState = state;
        state = State.STOPPING;
        try {
            log.info("Stopping framework");
            doStop();
            state = oldState == State.FAILED ? State.FAILED : State.TERMINATED;
            log.info("Framework stopped");
        /* CHECKSTYLE:OFF */
        } catch (Exception e) {
        /* CHECKSTYLE:ON */
            state = State.FAILED;
            log.warn("Stopping framework failed", e);
        }
    }
    
    /**
     * Template method for {@link Framework#stop()}.
     */
    protected abstract void doStop();
    

}
