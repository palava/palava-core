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
import java.net.URL;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;

import com.google.inject.name.Named;

import de.cosmocode.palava.core.service.Service;

/**
 * 
 *
 * @author Willi Schoenborn
 */
public class HibernateService implements Service {

    private final SessionFactory sessionFactory;

    public HibernateService(@Named("hibernate.cfg") File cfg, @Named("hibernate.schema") URL schema) {
        final Configuration configuration = new AnnotationConfiguration();
        configuration.addURL(schema);
        configuration.configure(cfg);
        this.sessionFactory = configuration.buildSessionFactory();
    }
    
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

}

