package de.cosmocode.palava.jobs.system;

import java.util.Map;
import java.util.Random;

import de.cosmocode.palava.ConnectionLostException;
import de.cosmocode.palava.DataRequest;
import de.cosmocode.palava.Job;
import de.cosmocode.palava.PHPContent;
import de.cosmocode.palava.Request;
import de.cosmocode.palava.Response;
import de.cosmocode.palava.Server;
import de.cosmocode.palava.Session;


/**
 * test job, let the thread sleep some time
 * @author Detlef Hüttemann
 */
public class sleep implements Job
{

	public void process(Request request, Response response, Session session, Server server, Map<String,Object> caddy) throws ConnectionLostException, Exception
	{
		DataRequest req = (DataRequest) request;	

		Map<String,String> args = req.getArgs();

		int delay = 0;

		try {
			delay = Integer.parseInt( args.get("msec") );
		} catch ( Exception e ) {
		}
		if ( delay == 0 ) {
			Random rnd = new Random();
			try {
				delay = rnd.nextInt(Integer.parseInt( args.get("random"))) ;
			} catch ( Exception e ) {}
		}
		try
		{
			if ( delay > 0 )
				Thread.currentThread().sleep( delay );
		}
		catch (Exception e)
		{}
		// send the output
        response.setContent( PHPContent.OK ) ;
	}

}
