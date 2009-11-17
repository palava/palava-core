package de.cosmocode.palava.jobs.system;

import java.util.Map;

import de.cosmocode.palava.ConnectionLostException;
import de.cosmocode.palava.Job;
import de.cosmocode.palava.PHPContent;
import de.cosmocode.palava.Request;
import de.cosmocode.palava.Response;
import de.cosmocode.palava.Server;
import de.cosmocode.palava.Session;


/**
 * returns the server's version
 * @author Tobias Sarnowski
 */
public class version implements Job
{

	public void process(Request request, Response response, Session session, Server server, Map<String,Object> caddy) throws ConnectionLostException, Exception
	{
        response.setContent(new PHPContent(server.getVersion()));
	}

}
