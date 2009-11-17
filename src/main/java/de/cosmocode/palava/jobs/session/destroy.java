package de.cosmocode.palava.jobs.session;

import java.util.Map;

import org.apache.log4j.Logger;

import de.cosmocode.palava.Job;
import de.cosmocode.palava.PHPContent;
import de.cosmocode.palava.Request;
import de.cosmocode.palava.Response;
import de.cosmocode.palava.Server;
import de.cosmocode.palava.Session;


/**
 * deletes the actual session
 * @author Tobias Sarnowski
 */
public class destroy implements Job
{

    private static final Logger logger = Logger.getLogger( destroy.class ) ;

    public void process( Request request, Response resp, Session session, Server server, Map<String,Object> caddy ) throws Exception
	{
		session.invalidate();
        resp.setContent(new PHPContent(PHPContent.OK)) ;
    }
}
