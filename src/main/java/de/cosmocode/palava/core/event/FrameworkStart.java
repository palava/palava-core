package de.cosmocode.palava.core.event;

import de.cosmocode.collections.Procedure;

/**
 * Event interface for framework stop.
 *
 * @author Willi Schoenborn
 */
public interface FrameworkStart {

    Procedure<FrameworkStart> PROCEDURE = new Procedure<FrameworkStart>() {
        
        @Override
        public void apply(FrameworkStart input) {
            input.eventFrameworkStart();
        }
        
    };

    /**
     * Event callback.
     */
    void eventFrameworkStart();
    
}
