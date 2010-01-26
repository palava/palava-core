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

package de.cosmocode.palava.core.lifecycle;

/**
 * A Service which implements {@link Suspendable} marks
 * that it is possible to suspend and resume.
 *
 * <p>
 *   This interface is part of the palava lifecycle framework.
 * </p>
 *
 * @author Willi Schoenborn
 */
public interface Suspendable extends Startable {

    /**
     * Suspends the service.
     * 
     * @throws LifecycleException if suspend failed
     */
    void suspend();
    
    /**
     * Resumes the service.
     * 
     * @throws LifecycleException if resume failed
     */
    void resume();
    
}