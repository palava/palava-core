package de.cosmocode.palava.jobs.captcha;

import java.util.Map;

import de.cosmocode.palava.ConnectionLostException;
import de.cosmocode.palava.DataRequest;
import de.cosmocode.palava.Job;
import de.cosmocode.palava.PHPContent;
import de.cosmocode.palava.Request;
import de.cosmocode.palava.Response;
import de.cosmocode.palava.Server;
import de.cosmocode.palava.Session;
import de.cosmocode.palava.components.captcha.Captcha;

public class validate implements Job {

	@Override
	public void process(Request request, Response response, Session session,
			Server server, Map<String, Object> caddy)
			throws ConnectionLostException, Exception {
		
        Captcha captcha = server.components.lookup(Captcha.class);

		DataRequest req = (DataRequest) request;
		Map<?, ?> args = req.getArgs();
		
		String userInput = (String) args.get("code");

		Boolean result = captcha.validate( session.getSessionID(), userInput );
		
		response.setContent( new PHPContent(result));
		


	}

}
