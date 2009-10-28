package de.cosmocode.palava.jobs;

import java.util.Map;

import de.cosmocode.palava.CachableJob;
import de.cosmocode.palava.ConnectionLostException;
import de.cosmocode.palava.DataRequest;
import de.cosmocode.palava.MissingArgumentException;
import de.cosmocode.palava.Request;
import de.cosmocode.palava.Response;
import de.cosmocode.palava.Server;
import de.cosmocode.palava.Session;

public abstract class CachableDataJob extends CachableJob {
	
	private Map<String, String> args;

	@Override
	@SuppressWarnings("unchecked")
	public final void process(Request request, Response response, Server server, Session session, 
		Map<String, Object> caddy) throws ConnectionLostException, Exception {
		
		DataRequest dataRequest = (DataRequest) request;
		args = dataRequest.getArgs();
		
		process(args, response, session, server, caddy);
	}
	
	protected abstract void process(Map<String, String> args, Response response, Session session, Server server, 
		Map<String, Object> caddy) throws ConnectionLostException, Exception;
    
    
    
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