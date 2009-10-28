package de.cosmocode.palava.jobs;

import java.util.Map;

import org.hibernate.Session;

import de.cosmocode.palava.DataRequest;
import de.cosmocode.palava.MissingArgumentException;
import de.cosmocode.palava.Request;
import de.cosmocode.palava.Response;
import de.cosmocode.palava.Server;
import de.cosmocode.palava.jobs.hib.HibJob;

public abstract class DataHibJob extends HibJob {

	private Map<String, String> args;
	
	@Override
	@SuppressWarnings("unchecked")
	public final void process(Request request, Response response, de.cosmocode.palava.Session s, Server server, 
		Map<String, Object> caddy, Session session) throws Exception {

		DataRequest dataRequest = (DataRequest) request;
		args = dataRequest.getArgs();
		
		if (session == null) session = createHibSession(server, caddy);
		
		process(args, response, s, server, caddy, session);
		session.flush();
	}
	
	protected final void validate(String... keys) throws MissingArgumentException {
		for (String key : keys) {
			if (!args.containsKey(key)) throw new MissingArgumentException(key);
		}
	}
	
	protected abstract void process(Map<String, String> args, Response response, de.cosmocode.palava.Session s, Server server,
		Map<String, Object> caddy, Session session) throws Exception;
	
}