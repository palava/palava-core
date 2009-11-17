package de.cosmocode.palava.jobs.system;

import java.util.Map;

import de.cosmocode.palava.Closable;
import de.cosmocode.palava.CloseConnection;
import de.cosmocode.palava.ConnectionLostException;
import de.cosmocode.palava.Job;
import de.cosmocode.palava.PHPContent;
import de.cosmocode.palava.Request;
import de.cosmocode.palava.Response;
import de.cosmocode.palava.Server;
import de.cosmocode.palava.Session;


/**
 * close the connection
 * @author Tobias Sarnowski
 */
public class close implements Job {
	
	public void process(Request request, Response response, Session s, Server server, 
		Map<String,Object> caddy) throws ConnectionLostException, CloseConnection, Exception {
		
		response.setContent(PHPContent.OK);
		response.send();
		
		for (Object value : caddy.values()) {
			if (value instanceof Closable) {
				Closable.class.cast(value).onClose();
			}
		}
		
		throw CloseConnection.getInstance();
	}

}
