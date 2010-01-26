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
 * Utility class for condition checks inside
 * the lifecycle framework. 
 *
 * @author Willi Schoenborn
 */
public final class Lifecycle {

    private Lifecycle() {
        
    }
   
    /**
     * Checks the given expression and throws a {@link LifecycleException}
     * if it is false. 
     * 
     * @param expression the condition
     * @param message the error message
     * @throws LifecycleException if expression is false
     */
    public static void check(boolean expression, String message) {
        if (expression) return;
        throw new LifecycleException(message);
    }
    
}
