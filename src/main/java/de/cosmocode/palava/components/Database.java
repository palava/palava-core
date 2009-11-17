package de.cosmocode.palava.components;

import java.sql.Connection;

import javax.sql.DataSource;

import org.jdom.Element;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import de.cosmocode.palava.Component;
import de.cosmocode.palava.ComponentException;
import de.cosmocode.palava.ComponentManager;
import de.cosmocode.palava.Server;


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

    public void initialize () throws Exception
	{

        // check availability of connections
        //
        Connection conn = ds.getConnection();

        conn.close();
    }
    
    public DataSource getDataSource() {
    	return ds;
    }
    
}
