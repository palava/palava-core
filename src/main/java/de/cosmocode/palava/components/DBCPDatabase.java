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
