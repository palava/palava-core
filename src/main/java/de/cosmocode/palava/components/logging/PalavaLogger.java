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