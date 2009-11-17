package de.cosmocode.palava.jobs.session;

import java.util.Map;

import org.apache.log4j.Logger;

import de.cosmocode.palava.Benchmark;
import de.cosmocode.palava.ClientData;
import de.cosmocode.palava.DataRequest;
import de.cosmocode.palava.Job;
import de.cosmocode.palava.PHPContent;
import de.cosmocode.palava.Request;
import de.cosmocode.palava.Response;
import de.cosmocode.palava.Server;
import de.cosmocode.palava.Session;


/**
 * checks session and creates new if necessary. 
 * requires datarequest. return phpcontent(sessionid)
 * @author Detlef Hüttemann
 */
public class initialize implements Job {

    private static final Logger logger = Logger.getLogger( initialize.class ) ;

	public void process( Request request, Response response, Session session, Server server, 
            Map<String,Object> caddy ) throws Exception {
        
        DataRequest dRequest = (DataRequest) request;
        Map<String, String> args = dRequest.getArgs();
        
        if (args.containsKey("url")) {
            String url = args.get("url");
            Benchmark.setURL(url);
        }
        
        if ( session != null ) {

            ClientData clientData = session.getClientData() ;

            if ( clientData == null ) {
                logger.error("no clientData");
                session = null;
            } else {
                if (!clientData.isValid(args)) {
                    logger.info("sessions client data differs");
                    session = null;
                }
            }
        }

        if ( session == null ) {
            session = server.sessions.createSession();
            session.setClientData(new ClientData(args));
        }
        
        // send current sessionid as response
        //
        //

        response.setContent( new PHPContent( session.getSessionID() ) ) ;

    }
}
