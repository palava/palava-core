package de.cosmocode.palava.core;

/**
 * Tests {@link DefaultRegistry}.
 *
 * @author Willi Schoenborn
 */
public final class DefaultRegistryTest extends AbstractRegistryTest {

    @Override
    protected Registry unit() {
        return new DefaultRegistry();
    }

    @Override
    protected boolean supportsLiveView() {
        return true;
    }

}
