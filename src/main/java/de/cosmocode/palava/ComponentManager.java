package de.cosmocode.palava;

public interface ComponentManager {

    void initialize() throws Exception;

    <T extends Component> T lookup(Class<T> component);
    
    <T extends Component> T lookup(Class<T> spec, String name);
    
}
