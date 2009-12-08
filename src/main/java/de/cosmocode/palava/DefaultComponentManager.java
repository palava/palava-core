/**
 * palava - a java-php-bridge
 * Copyright (C) 2007  CosmoCode GmbH
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package de.cosmocode.palava;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

/**
 * manages all components
 * @author Detlef Hüttemann
 */
public class DefaultComponentManager implements ComponentManager {

    private static final Logger log = LoggerFactory.getLogger(ComponentManager.class);
    
    private final Server server;
    private final Document document;
    
    private final Map<String, Component> components = Maps.newLinkedHashMap();

    /**
     * TODO throw meaningful exception
     * 
     * @param file
     * @param server
     * @throws JDOMException
     * @throws IOException
     */
    public DefaultComponentManager(File file, Server server) throws JDOMException, IOException {
        this.server = server;
        document = new SAXBuilder().build(file);
    }

    public void initialize() throws Exception {
        final Element root = document.getRootElement();
        
        log.info("ComponentManager initialize start");

        final List<?> children = root.getChildren();

        for (final Iterator<?> iterator = children.iterator(); iterator.hasNext(); ) {
            final Element element = (Element) iterator.next();
            final String clazz = element.getAttribute("class").getValue();
            final Component component = Class.forName(clazz).asSubclass(Component.class).newInstance();

            log.info("ComponentManager configure " + element.getName());
            component.configure(element, server);
            components.put(element.getName(), component);
        }

        for (ComponentInterceptor interceptor : ServiceLoader.load(ComponentInterceptor.class)) {
            log.debug("Running interceptor: {}", interceptor);
            
            for (Component component : components.values()) {
                interceptor.intercept(this, component);
            }
        }
        
        for (Component component : components.values()) {
            log.info("ComponentManager compose " + getComponentName(component, component.getClass().getName()));
            component.compose(this);
        }

        for (Component component : components.values()) {
            log.info("ComponentManager initialize " + getComponentName(component, component.getClass().getName()));
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

    private String getComponentName(Component instance, String notFoundValue) {
        for (Map.Entry<String, Component> me : components.entrySet()) {
            if (me.getValue() == instance) return me.getKey();
        }
        return notFoundValue;
    }
    
    @Override
    public void shutdown() {
        for (Component component : components.values()) {
            if (component instanceof ManagedService) {
                ManagedService.class.cast(component).shutdown();
            }
        }
    }
}
