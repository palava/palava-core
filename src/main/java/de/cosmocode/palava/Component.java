package de.cosmocode.palava;


/**
 * @author Detlef Hüttemann
 */
public interface Component
{
    public void configure(org.jdom.Element root, Server server) throws ComponentException;
    public void compose(ComponentManager manager) throws ComponentException;
    public void initialize() throws Exception;
}
