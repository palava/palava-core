package de.cosmocode.palava.jobs;

import java.util.Map;

import org.json.JSONObject;

import de.cosmocode.palava.JSONRequest;
import de.cosmocode.palava.MissingArgumentException;
import de.cosmocode.palava.Request;
import de.cosmocode.palava.Response;
import de.cosmocode.palava.Server;
import de.cosmocode.palava.Session;
import de.cosmocode.palava.jobs.hib.HibJob;

public abstract class JSONHibJob extends HibJob {
	
	private JSONObject json;

	@Override
	public final void process(Request request, Response response, Session s, Server server, 
		Map<String, Object> caddy, org.hibernate.Session session) throws Exception {

		JSONRequest jRequest = (JSONRequest) request;
		json = jRequest.getJSONObject();
		
		if (session == null) session = createHibSession(server, caddy);

		process(json, response, s, server, caddy, session);
		session.flush();
	}
	
	protected final void require(String... keys) throws MissingArgumentException {
        for (String key : keys) {
            if (!json.has(key)) throw new MissingArgumentException(key);
        }
	}
	
	protected abstract void process(JSONObject json, Response response, Session s, Server server,
		Map<String, Object> caddy, org.hibernate.Session session) throws Exception;

}