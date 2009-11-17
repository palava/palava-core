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
package de.cosmocode.palava.components;

import java.sql.Connection;

import org.apache.commons.dbcp.BasicDataSource;
import org.jdom.Element;

import de.cosmocode.palava.Component;
import de.cosmocode.palava.ComponentException;
import de.cosmocode.palava.ComponentManager;
import de.cosmocode.palava.Server;


/**
 * pooled mysql database component
 * @author Detlef Hüttemann
 */
public class DBCPDatabase extends Database implements Component
{

    //public DataSource ds;

    public void configure (Element elem, Server server) throws ComponentException
	{
        Element username = elem.getChild("username");
        if ( username == null ) throw new IllegalArgumentException("missing username");
        
		Element password = elem.getChild("password");
        if ( password == null ) throw new IllegalArgumentException("missing password");
        Element url = elem.getChild("url");
        if ( url == null ) throw new IllegalArgumentException("missing url");
        Element driver = elem.getChild("driver");
        if ( driver == null ) throw new IllegalArgumentException("missing driver");

        BasicDataSource bds = new BasicDataSource();

        bds.setUsername( username.getText() );
        bds.setPassword( password.getText() );
        bds.setUrl( url.getText() );
        bds.setDriverClassName( driver.getText() );

        ds = bds;
    }

    public void compose (ComponentManager mngr) {}

    public void initialize () throws Exception
	{
        // check availability of connections
        //
        Connection conn = ds.getConnection();
        conn.close();
    }
}
