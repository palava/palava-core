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
 * Static utility class for {@link Executable}s.
 *
 * @since 2.1
 * @author Willi Schoenborn
 */
public final class Executables {

    private Executables() {
        
    }
    
    /**
     * Adapts an {@link Executable} to the {@link Runnable} interface.
     * 
     * @param executable the backing executable
     * @return a runnable backed by the specified executable
     */
    public static Runnable asRunnable(final Executable executable) {
        return new Runnable() {
            
            @Override
            public void run() {
                try {
                    executable.execute();
                } catch (LifecycleException e) {
                    throw new IllegalStateException(e);
                }
            }
            
        };
    }
    
    /**
     * Adapts a {@link Runnable} to the {@link Executable} interface.
     * 
     * @param runnable the backing runnable
     * @return an executable backed by the specified executable
     */
    public static Executable asExecutable(final Runnable runnable) {
        return new Executable() {
            
            @Override
            public void execute() throws LifecycleException {
                try {
                    runnable.run();
                /* CHECKSTYLE:OFF */
                } catch (RuntimeException e) {
                /* CHECKSTYLE:ON */
                    throw new LifecycleException(e);
                }
            }
            
        };
    }

}
