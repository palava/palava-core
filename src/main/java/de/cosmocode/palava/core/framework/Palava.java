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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package de.cosmocode.palava.core.framework;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Static factory class for framework instances.
 *
 * @author Willi Schoenborn
 */
public final class Palava {

    private static final Logger log = LoggerFactory.getLogger(Palava.class);
    
    private Palava() {
        
    }

    /**
     * Constructs a new {@link Framework} using the specified properties.
     * 
     * @param settings the settings
     * @return a new configured {@link Framework} instance
     * @throws NullPointerException if properties is null
     */
    public static Framework createFramework(Properties settings) {
        log.debug("Creating new framework using {}", settings);
        return new DefaultFramework(settings);
    }
    
}
