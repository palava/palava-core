package de.cosmocode.palava.core.lifecycle;

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
     * @param properties the settings
     * @return a new configured {@link Framework} instance
     * @throws NullPointerException if properties is null
     */
    public static Framework createFramework(Properties properties) {
        log.debug("Creating new framework using {}", properties);
        return new DefaultFramework(properties);
    }
    
}
