package de.cosmocode.palava.jobs.session;

import java.util.Map;

import de.cosmocode.palava.ConnectionLostException;
import de.cosmocode.palava.DataRequest;
import de.cosmocode.palava.Job;
import de.cosmocode.palava.PHPContent;
import de.cosmocode.palava.Request;
import de.cosmocode.palava.Response;
import de.cosmocode.palava.Server;
import de.cosmocode.palava.Session;

public class remove implements Job {

	@Override
	public void process(Request request, Response response, Session session,
			Server server, Map<String, Object> caddy)
			throws ConnectionLostException, Exception {
		
        DataRequest req = (DataRequest) request;

        if ( session == null ) throw new NullPointerException("session");

        Map<String,Object> args = req.getArgs() ;
        
        for ( String key : args.keySet()){
        	session.remove(key);
        }
        response.setContent( PHPContent.OK ) ;
 

	}

}
