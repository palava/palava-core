package de.cosmocode.palava.jobs;

import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import de.cosmocode.palava.JSONRequest;
import de.cosmocode.palava.MissingArgumentException;
import de.cosmocode.palava.Request;
import de.cosmocode.palava.Response;
import de.cosmocode.palava.Server;
import de.cosmocode.palava.Session;
import de.cosmocode.palava.jobs.hib.CachableHibJob;

public abstract class CachableJSONHibJob extends CachableHibJob {
	
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
    
    protected abstract void process(JSONObject json, Response response, Session s, Server server,
        Map<String, Object> caddy, org.hibernate.Session session) throws Exception;
    
	
	protected final void validate(JSONObject json, String... keys) throws MissingArgumentException {
		for (String key : keys) {
			if (!json.has(key)) throw new MissingArgumentException(key);
		}
	}
    

    // methods implemented from UtilityJob

    public String getMandatory(String key) throws MissingArgumentException, JSONException {
        if (json.has(key)) return json.getString(key);
        else throw new MissingArgumentException(this, key);
    }

    public String getMandatory(String key, String argumentType) throws MissingArgumentException, JSONException {
        if (json.has(key)) return json.getString(key);
        else throw new MissingArgumentException(this, key, argumentType);
    }

    public String getOptional(String key) {
        if (json.has(key)) return json.optString(key);
        else return null;
    }

    public String getOptional(String key, String defaultValue) {
        if (json.has(key)) return json.optString(key);
        else return defaultValue;
    }

    public boolean hasArgument(String key) {
        return json.has(key);
    }

}