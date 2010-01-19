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
import java.sql.SQLException;

import javax.sql.DataSource;

import org.jdom.Element;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import de.cosmocode.palava.Component;
import de.cosmocode.palava.ComponentException;
import de.cosmocode.palava.ComponentManager;
import de.cosmocode.palava.core.server.Server;


/**
 * direct mysql database connection component
 * copied and edited from the DBCPDatabase component
 * @author Tobias Sarnowski
 */
public class Database implements Component {

    public DataSource ds;
    
    public String username;
    public String password;
    public String server;
    public String database;

    public void configure (Element elem, Server s) throws ComponentException
	{
        Element username = elem.getChild("username");
        if ( username == null ) throw new IllegalArgumentException("missing username");
        Element password = elem.getChild("password");
        if ( password == null ) throw new IllegalArgumentException("missing password");
        Element server = elem.getChild("server");
        if ( server == null ) throw new IllegalArgumentException("missing server");
        Element database = elem.getChild("database");
        if ( database == null ) throw new IllegalArgumentException("missing database");

        final MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setServerName(this.server = server.getText());
        dataSource.setUser(this.username = username.getText());
        dataSource.setPassword(this.password = password.getText());
        dataSource.setDatabaseName(this.database = database.getText());

        ds = dataSource;

    }

    public void compose (ComponentManager mngr) {}

    public void initialize ()  {
        try {
            final Connection c = ds.getConnection();
            c.close();
        } catch (SQLException e) {
            throw new ComponentException(e);
        }
    }
    
    public DataSource getDataSource() {
    	return ds;
    }
    
}
