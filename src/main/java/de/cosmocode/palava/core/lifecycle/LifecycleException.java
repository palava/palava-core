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
 * Indicates an error in a service lifycycle.
 *
 * @author Willi Schoenborn
 */
public class LifecycleException extends RuntimeException {

    private static final long serialVersionUID = 4658363777699992043L;

    public LifecycleException(String message) {
        super(message);
    }
    
    public LifecycleException(Throwable throwable) {
        super(throwable);
    }
    
    public LifecycleException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
