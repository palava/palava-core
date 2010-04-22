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
    void suspend() throws LifecycleException;
    
    /**
     * Resumes the service.
     * 
     * @throws LifecycleException if resume failed
     */
    void resume() throws LifecycleException;
    
    /**
     * {@inheritDoc}
     * Stopping a suspended service has no effect.
     */
    @Override
    void stop() throws LifecycleException;
    
}
