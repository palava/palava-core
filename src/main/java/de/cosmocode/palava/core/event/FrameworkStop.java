package de.cosmocode.palava.core.event;

import de.cosmocode.collections.Procedure;

/**
 * Event interface for framework stop.
 *
 * @author Willi Schoenborn
 */
public interface FrameworkStop {

    Procedure<FrameworkStop> PROCEDURE = new Procedure<FrameworkStop>() {
        
        @Override
        public void apply(FrameworkStop input) {
            input.eventFrameworkStop();
        }
        
    };
    
    /**
     * Event callback.
     */
    void eventFrameworkStop();
    
}
