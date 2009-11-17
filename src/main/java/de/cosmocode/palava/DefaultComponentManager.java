package de.cosmocode.palava;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * manages all components
 * @author Detlef Hüttemann
 */
public class DefaultComponentManager implements ComponentManager {

    private static final Logger log = LoggerFactory.getLogger(ComponentManager.class);
    
	private final Server server;
	private final Document document;
	
    private final Map<String,Component> components = new LinkedHashMap<String,Component>();

    public DefaultComponentManager (File file, Server server) throws Exception {
		this.server = server;
        document = new SAXBuilder().build(file);
    }

    public void initialize() throws Exception {
        final Element root = document.getRootElement();
        
        log.info("ComponentManager initialize start");

        List<?> children = root.getChildren();

        // construct
        //
        for (Iterator<?> iter = children.iterator(); iter.hasNext(); ) {
            Element elem = (Element) iter.next();
                String clazz = elem.getAttribute("class").getValue();
                Component component = (Component)Class.forName(clazz).newInstance();

                log.info("ComponentManager configure " + elem.getName() );
                component.configure(elem, server);
                components.put(elem.getName(), component);
        }

        for (ComponentInterceptor interceptor : ServiceLoader.load(ComponentInterceptor.class)) {
            log.debug("Running interceptor: {}", interceptor);
            
            for (Component component : components.values()) {
                    interceptor.intercept(this, component);
            }
        }
        
        // compose 
        //
        for (Component component : components.values()) {
            log.info("ComponentManager compose " + getComponentName( component, component.getClass().getName() ) );
            component.compose(this);
        }

        // initialize 
        //
        for (Component component : components.values()) {
            log.info("ComponentManager initialize " + getComponentName( component, component.getClass().getName() ) );
            component.initialize();
        }
        log.info("ComponentManager initialize finished");
    }

    public <T extends Component> T lookup(Class<T> component) {
    	return lookup(component, component.getSimpleName());
    }
    
    public <T extends Component> T lookup(Class<T> spec, String name) {
        final T t = spec.cast(components.get(name));
        if (t == null) throw new ComponentNotFoundException(name);
        return t;
    }

    private String getComponentName( Component instance, String notFoundValue ) {
        for( Map.Entry<String,Component> me :components.entrySet() ) {
            if ( me.getValue() == instance ) return me.getKey();
        }
        return notFoundValue;
    }
}
