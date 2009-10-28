package de.cosmocode.palava.jobs;

import java.util.Map;

import org.json.JSONObject;

import de.cosmocode.palava.ConnectionLostException;
import de.cosmocode.palava.JSONRequest;
import de.cosmocode.palava.Job;
import de.cosmocode.palava.MissingArgumentException;
import de.cosmocode.palava.Request;
import de.cosmocode.palava.Response;
import de.cosmocode.palava.Server;
import de.cosmocode.palava.Session;

public abstract class JSONJob implements Job {
	
	private JSONObject json;

	@Override
	public final void process(Request request, Response response, Session session, Server server, 
		Map<String, Object> caddy) throws ConnectionLostException, Exception {

		JSONRequest jRequest = (JSONRequest) request;
		json = jRequest.getJSONObject();
		
		process(json, response, session, server, caddy);
	}
	
	protected final void require(String... keys) throws MissingArgumentException {
		require(json, keys);
	}
	
	protected final void require(JSONObject json, String... keys) throws MissingArgumentException {
		for (String key : keys) {
			if (!json.has(key)) throw new MissingArgumentException(key);
		}
	}
	
	protected abstract void process(JSONObject json, Response response, Session session, Server server, 
		Map<String, Object> caddy) throws ConnectionLostException, Exception;
	
}