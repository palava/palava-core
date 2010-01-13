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

package de.cosmocode.palava.core.intercept;

import java.util.List;

import org.jdom.Element;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.inject.spi.InjectionListener;

import de.cosmocode.palava.Component;
import de.cosmocode.palava.ComponentException;
import de.cosmocode.palava.Server;
import de.cosmocode.palava.Service;
import de.cosmocode.palava.core.service.lifecycle.Configurable;
import de.cosmocode.palava.core.service.lifecycle.Initializable;

/**
 * This {@link InjectionListener} allows run the configure/initalize
 * methods on services.
 *
 * @author Willi Schoenborn
 */
public final class LifycycleInjectionListener implements InjectionListener<Object> {

    private final Element root;
    private final Server server;
    
    public LifycycleInjectionListener(Element root, Server server) {
        this.root = Preconditions.checkNotNull(root, "Root");
        this.server = Preconditions.checkNotNull(server, "Server");
    }
    
    @Override
    public void afterInjection(final Object injectee) {
        @SuppressWarnings("unchecked")
        final List<Element> elements = root.getChildren();
        final Element element = Iterables.find(elements, new Predicate<Element>() {
            
            @Override
            public boolean apply(Element input) {
                return injectee.getClass().getName().equals(input.getAttributeValue("impl"));
            }
            
        });
        
        // configuration
        if (injectee instanceof Configurable) {
            Configurable.class.cast(injectee).configure(element);
        } else if (injectee instanceof Component) {
            try {
                Component.class.cast(injectee).configure(element, server);
            } catch (ComponentException e) {
                throw new IllegalArgumentException(e);
            }
        } else if (injectee instanceof Service) {
            Service.class.cast(injectee).configure(element, server);
        }

        // initialization
        if (injectee instanceof Initializable) {
            Initializable.class.cast(injectee).initialize();
        } else if (injectee instanceof Component) {
            Component.class.cast(injectee).initialize();
        } else if (injectee instanceof Service) {
            Service.class.cast(injectee).initialize();
        }
    }

    @Override
    public String toString() {
        return getClass().getName();
    }
    
}
