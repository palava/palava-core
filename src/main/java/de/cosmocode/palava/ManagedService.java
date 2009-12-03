package de.cosmocode.palava;

import org.jdom.Element;

/**
 * A {@link ManagedService} is a {@link Component}
 * with more lifecycle methods.
 *
 * @author Willi Schoenborn
 */
public interface ManagedService extends Component {

    @Override
    void configure(Element root, Server server);
    
    /**
     * {@inheritDoc}
     * 
     * @deprecated use the Service annotation instead
     */
    @Deprecated
    @Override
    void compose(ComponentManager manager);
    
    @Override
    void initialize();
    
    /**
     * Called when the server is about to shutdown.
     */
    void shutdown();
    
}
