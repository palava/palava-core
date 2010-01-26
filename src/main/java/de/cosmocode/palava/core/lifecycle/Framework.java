package de.cosmocode.palava.core.lifecycle;

import de.cosmocode.commons.Stateful;

/**
 * Root type for the palava framework.
 *
 * @author Willi Schoenborn
 */
public interface Framework extends Stateful {

    /**
     * Starts the framework.
     */
    void start();
    
    /**
     * Stops the framework.
     */
    void stop();

}
