package de.cosmocode.palava;

import java.util.Map;


/**
 * every job has to have a process() function
 * @author Tobias Sarnowski
 */
public interface Job
{

	/**
	 * @param request the request of this job. contains the invoking args
	 * @param response the container for the results 
	 * @param session a session (may be null). available across different frontent/http requests
	 * @param server the server structure - basically for the components lookup
	 * @param caddy a container available across all jobs of the <strong>same</strong> frontend/http request
	 * @throws ConnectionLostException
	 * @throws Exception
	 */
	public void process(Request request, Response response, Session session, Server server, Map<String,Object> caddy ) throws ConnectionLostException, Exception;

}
