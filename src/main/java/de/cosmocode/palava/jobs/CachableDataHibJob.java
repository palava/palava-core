package de.cosmocode.palava.jobs;

import java.util.Map;

import org.hibernate.Session;

import de.cosmocode.palava.DataRequest;
import de.cosmocode.palava.MissingArgumentException;
import de.cosmocode.palava.Request;
import de.cosmocode.palava.Response;
import de.cosmocode.palava.Server;
import de.cosmocode.palava.jobs.hib.CachableHibJob;

public abstract class CachableDataHibJob extends CachableHibJob {

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
	
	protected abstract void process(Map<String, String> args, Response response, de.cosmocode.palava.Session s, Server server,
		Map<String, Object> caddy, Session session) throws Exception;
    
    

    // methods implemented from UtilityJob

    public String getMandatory(String key) throws MissingArgumentException {
        if (args.containsKey(key)) return args.get(key);
        else throw new MissingArgumentException(this, key);
    }

    public String getMandatory(String key, String argumentType) throws MissingArgumentException {
        if (args.containsKey(key)) return args.get(key);
        else throw new MissingArgumentException(this, key, argumentType);
    }

    public String getOptional(String key) {
        return args.get(key);
    }

    public String getOptional(String key, String defaultValue) {
        if (args.containsKey(key)) return args.get(key);
        else return defaultValue;
    }

    public boolean hasArgument(String key) {
        return args.containsKey(key);
    }
	
}