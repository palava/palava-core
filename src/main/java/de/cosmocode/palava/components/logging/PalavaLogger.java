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

package de.cosmocode.palava.components.logging;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.json.extension.JSONEncoder;

public abstract class PalavaLogger {

    private static final String PROPERTY_NAME = "palava.logging";
    private static final Logger log = Logger.getLogger(PalavaLogger.class);
    
    public static PalavaLogger getLogger() {
        
        String className = System.getProperty(PROPERTY_NAME);
        log.info(PROPERTY_NAME + ": " + className);
                
        if (className == null) {
            return new NullLogger();
        } else {
            try {
                return (PalavaLogger) Class.forName(className).newInstance();
            } catch (Exception e) {
                log.error("could not instantiate " + className, e);
                return null;
            }
        }
        
    }

    /**
     * 
     * @param session
     * @param objectID
     * @param objectType
     * @param operation
     */
    public void log(Session session, Long objectID, Class<?> objectType, Enum<? extends LogOperation> operation) {
        log(session, objectID, objectType, operation, null);
    }
    
    /**
     * 
     * @param session
     * @param objectID
     * @param objectType
     * @param operation
     * @param message
     */
    public void log(Session session, Long objectID, Class<?> objectType, Enum<? extends LogOperation> operation, String message) {
        log(session, objectID, objectType, operation, message, null);
    }

    /**
     * 
     * @param session
     * @param objectID
     * @param objectType
     * @param operation
     * @param message
     * @param json
     */
    public abstract void log(Session session, Long objectID, Class<?> objectType, Enum<? extends LogOperation> operation, String message, JSONEncoder json);
    
}