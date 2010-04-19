package de.cosmocode.palava.core;

import com.google.common.base.Preconditions;
import com.google.inject.Binder;
import com.google.inject.Module;

/**
 * Binds {@link Framework} to the given instance.
 *
 * @author Willi Schoenborn
 */
final class FrameworkModule implements Module {

    private final Framework framework;
    
    public FrameworkModule(Framework framework) {
        this.framework = Preconditions.checkNotNull(framework, "Framework");
    }

    @Override
    public void configure(Binder binder) {
        binder.bind(Framework.class).toInstance(framework);
    }

}
