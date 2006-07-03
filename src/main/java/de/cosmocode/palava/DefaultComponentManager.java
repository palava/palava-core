package de.cosmocode.palava;
/*
palava - a java-php-bridge
Copyright (C) 2007  CosmoCode GmbH

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/

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
