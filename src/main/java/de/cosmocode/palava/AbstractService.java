package de.cosmocode.palava;

import org.jdom.Element;

/**
 * An abstract implementation of the {@link ManagedService} interface
 * which implements all methods with an empty block.
 *
 * @author Willi Schoenborn
 */
public abstract class AbstractService implements ManagedService {

    @Override
    public void configure(Element root, Server server) {

    }
    
    @Override
    public final void compose(ComponentManager manager) {

    }

    @Override
    public void initialize() {

    }

    @Override
    public void shutdown() {

    }

}
