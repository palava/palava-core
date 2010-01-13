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

package de.cosmocode.palava.components.hib;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.jdom.Element;

import de.cosmocode.palava.Component;
import de.cosmocode.palava.ComponentException;
import de.cosmocode.palava.ComponentManager;
import de.cosmocode.palava.Server;

public class Hib implements Component {

    SessionFactory sessionFactory;

    @Override
    public void configure(Element root, Server server) throws ComponentException {
        
        String hibernateSchema = root.getChildText("hibernateSchema");
        if (hibernateSchema == null ) throw new NullPointerException("missing config entry 'hibernateSchema'");
        String hibernateConfiguration = root.getChildText("hibernateConfiguration");
        if (hibernateConfiguration == null ) throw new NullPointerException("missing config entry 'hibernateConfiguration'");
        
        
        hibernateConfiguration = server.getFilename( hibernateConfiguration );
        if ( ! hibernateSchema.startsWith("file://") ) {
            hibernateSchema = "file://" + server.getFilename( hibernateSchema );
        }

        AnnotationConfiguration cfg = new AnnotationConfiguration();
        try {
			cfg.addURL(new URL(hibernateSchema));
		} catch (MalformedURLException e) {
			throw new ComponentException(e.getMessage());
		}
        cfg.configure(new File(hibernateConfiguration));
        sessionFactory = cfg.buildSessionFactory();

    }

    @Override
    public void compose(ComponentManager manager) throws ComponentException {
    	
    }
    
    @Override
    public void initialize() {
        
    }
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

}

