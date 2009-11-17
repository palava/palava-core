package de.cosmocode.palava.jobs.session;

import java.util.Map;

import org.apache.log4j.Logger;

import de.cosmocode.palava.DataRequest;
import de.cosmocode.palava.Job;
import de.cosmocode.palava.PHPContent;
import de.cosmocode.palava.Request;
import de.cosmocode.palava.Response;
import de.cosmocode.palava.Server;
import de.cosmocode.palava.Session;


/**
 * sets a session data entry
 * @author Detlef Hüttemann
 */
public class set implements Job {

    private static final Logger logger = Logger.getLogger( set.class ) ;

    public void process( Request request, Response resp, Session session, Server server, Map<String,Object> caddy ) throws Exception {
        DataRequest req = (DataRequest) request;

        if ( session == null ) throw new NullPointerException("session");

        Map<String,Object> args = req.getArgs() ;

        session.putAll( (Map<String,Object>) args ) ;

        resp.setContent( PHPContent.OK ) ;
        
    }
}
