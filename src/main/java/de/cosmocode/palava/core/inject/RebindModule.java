package de.cosmocode.palava.core.inject;

import com.google.inject.Module;

/**
 * A {@link Module} which allows overriding defaults.
 *
 * @author Willi Schoenborn
 */
public interface RebindModule extends Module {

    /**
     * When called marks all optional dependencies of the underlying
     * installed services to be rebound.
     * 
     * @return a module which rebinds optional dependencies
     */
    Module overrideOptionals();
    
}
