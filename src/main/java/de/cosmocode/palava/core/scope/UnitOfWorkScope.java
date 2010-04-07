package de.cosmocode.palava.core.scope;

import com.google.inject.Provider;
import com.google.inject.Scope;

/**
 * A custom scope which defines an arbitrary unit of work.
 *
 * @author Willi Schoenborn
 */
public interface UnitOfWorkScope extends Scope, Provider<ScopeContext> {

    /**
     * Enters the scope.
     * 
     * @throws IllegalStateException if the scope is already in progress
     */
    void enter();
    
    /**
     * Exits the scope.
     * 
     * @throws IllegalStateException if there is no scoping block in progress
     */
    void exit();
    
}
