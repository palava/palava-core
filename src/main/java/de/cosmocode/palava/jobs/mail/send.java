package de.cosmocode.palava.jobs.mail;

import java.util.Map;

import de.cosmocode.palava.ConnectionLostException;
import de.cosmocode.palava.DataRequest;
import de.cosmocode.palava.Job;
import de.cosmocode.palava.PHPContent;
import de.cosmocode.palava.Request;
import de.cosmocode.palava.Response;
import de.cosmocode.palava.Server;
import de.cosmocode.palava.Session;
import de.cosmocode.palava.components.mail.Mailer;


/**
 * @author Detlef Huettemann
 */
public class send implements Job
{

	public void process(Request request, Response response, Session session, Server server, Map<String,Object> caddy) throws ConnectionLostException, Exception
	{
        Mailer mailer = server.components.lookup(Mailer.class);
        DataRequest req = (DataRequest) request;
        Map<String,Object> args = req.getArgs();

        String template = (String) args.get("template");
        String to = (String) args.get("to");

        mailer.sendMessage(template,"en",args,to);

        response.setContent(new PHPContent("ok"));
	}

}
