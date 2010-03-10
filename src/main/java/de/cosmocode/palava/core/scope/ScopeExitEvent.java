package de.cosmocode.palava.core.scope;

/**
 * Event interface for exiting scopes.
 *
 * @author Willi Schoenborn
 */
public interface ScopeExitEvent {

    /**
     * Being called when any scope is being exited.
     * 
     * @param context the scope's context
     */
    void eventScopeExit(ScopeContext context);
    
}
