package de.cosmocode.palava.core.scope;

/**
 * Event interface for entering scopes.
 *
 * @author Willi Schoenborn
 */
public interface ScopeEnterEvent {

    /**
     * Being called when any scope is being entered.
     * 
     * @param context the new scope context
     */
    void eventScopeEnter(ScopeContext context);
    
}
