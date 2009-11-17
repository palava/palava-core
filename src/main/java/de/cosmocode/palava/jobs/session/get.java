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


/**
 * get a session data entry
 * @author Detlef Hüttemann
 */
public class get implements Job
{

	public void process(Request request, Response response, Session session, Server server, Map<String,Object> caddy) throws ConnectionLostException, Exception
	{
        DataRequest req = (DataRequest) request;
        Map<String,String> args = req.getArgs();
        String key = args.get("key");
        if ( key == null ) throw new NullPointerException("key");
        Object data = session.get( key ) ;
        if ( data != null )
            response.setContent(new PHPContent(data));
        else 
            response.setContent( PHPContent.NOT_FOUND );
	}

}
