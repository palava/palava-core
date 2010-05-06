package de.cosmocode.palava.core;

import org.junit.Test;

import com.google.inject.Module;

/**
 * Tests {@link Palava}.
 *
 * @since 2.4 
 * @author Willi Schoenborn
 */
public final class PalavaTest {

    /**
     * Tests {@link Palava#newFramework()} with a missing application.properties.
     */
    @Test(expected = IllegalArgumentException.class)
    public void newFramework() {
        Palava.newFramework();
    }
    
    /**
     * Tests {@link Palava#newFramework(com.google.inject.Module)} with a missing application.properties.
     */
    @Test(expected = IllegalArgumentException.class)
    public void newFrameworkModule() {
        Palava.newFramework(new EmptyApplication());
    }
    
    /**
     * Tests {@link Palava#newFramework(com.google.inject.Module)} with a null module.
     */
    @Test(expected = NullPointerException.class)
    public void newFrameworkModuleNull() {
        final Module module = null;
        Palava.newFramework(module);
    }
    
    
    
}
