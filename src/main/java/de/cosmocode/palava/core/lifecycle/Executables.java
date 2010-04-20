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

package de.cosmocode.palava.core.lifecycle;


/**
 * Static utility class for {@link Executable}s.
 *
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
