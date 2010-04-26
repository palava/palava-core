package de.cosmocode.palava.core.inject;

import com.google.inject.Module;
import com.google.inject.PrivateModule;

/**
 * An abstract {@link PrivateModule} which forces implementors to specify every single
 * step when writing re-binding modules. This should reduce errors.
 *
 * @author Willi Schoenborn
 */
abstract class AbstractRebindingModule extends PrivateModule implements RebindModule {

    private boolean overridden;
    
    @Override
    protected final void configure() {
        configuration();
        if (overridden) optionals();
        bindings();
        expose();
    }
    
    /**
     * Rebinds configuration entries.
     */
    protected abstract void configuration();
    
    /**
     * Rebinds optional configuration entries.
     */
    protected abstract void optionals();
    
    /**
     * Configures bindings.
     */
    protected abstract void bindings();
    
    /**
     * Expose specific bindings.
     */
    protected abstract void expose();

    @Override
    public final Module overrideOptionals() {
        overridden = true;
        return this;
    }
    
}
