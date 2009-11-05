package de.cosmocode.palava.jobs;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

import de.cosmocode.palava.ConnectionLostException;
import de.cosmocode.palava.DataRequest;
import de.cosmocode.palava.Job;
import de.cosmocode.palava.MissingArgumentException;
import de.cosmocode.palava.Request;
import de.cosmocode.palava.Response;
import de.cosmocode.palava.Server;
import de.cosmocode.palava.Session;

public abstract class DataJob implements Job {
	
	private Map<String, String> args;

	@Override
	@SuppressWarnings("unchecked")
	public final void process(Request request, Response response, Session session, Server server, 
		Map<String, Object> caddy) throws ConnectionLostException, Exception {
		
		DataRequest dataRequest = (DataRequest) request;
		args = dataRequest.getArgs();
		
		process(args, response, session, server, caddy);
	}
	
	protected final void validate(String... keys) throws MissingArgumentException {
		for (String key : keys) {
			if (!args.containsKey(key)) throw new MissingArgumentException(key);
		}
	}
	
	protected abstract void process(Map<String, String> args, Response response, Session session, Server server, 
		Map<String, Object> caddy) throws ConnectionLostException, Exception;
    
    
    
    // ------------------------------------
    // helper methods for the job-arguments
    // ------------------------------------
    
    /** 
     * lookup a necessary parameter.
     * @throws MissingArgumentException if the necessary parameter was omitted
     */
    public String lookup (String key) throws MissingArgumentException {
        if (args.containsKey(key))
            return args.get(key);
        else throw new MissingArgumentException(this, key);
    }
    /** 
     * lookup a necessary parameter.
     * @throws MissingArgumentException if the necessary parameter was omitted
     */
    public String lookup (String key, String argumentType) throws MissingArgumentException {
        if (args.containsKey(key))
            return args.get(key);
        else throw new MissingArgumentException(this, key, argumentType);
    }
    
    /** lookup an optional parameter (equivalent to args.get(key)) */
    public String lookupOptional (String key) {
        return args.get(key);
    }

    /** lookup an optional parameter. if it doesn't exist, defaultValue is returned. */
    public String lookupOptional (String key, String defaultValue) {
        if (args.containsKey(key)) return args.get(key);
        else                            return defaultValue;
    }

    /** 
     * lookup a necessary parameter and parse it to bool
     * <br>Parsing works like this: 
     * <table>
     * <tr><td>"false"</td>          <td>false</td></tr>
     * <tr><td>null</td>             <td>false</td></tr>
     * <tr><td>"", " ", ...</td>     <td>false</td></tr>
     * <tr><td>(everything else)</td><td>true</td></tr>
     * </table>
     * @throws MissingArgumentException if the necessary parameter was omitted
     */
    public boolean lookupBool (String key) throws MissingArgumentException {
        if (args.containsKey(key))
            return lookupOptionalBool(key);
        else throw new MissingArgumentException(this, key, "boolean");
    }
    /** lookup an optional bool.
     * <br>Parsing works like this: 
     * <table>
     * <tr><td>"false"</td>          <td>false</td></tr>
     * <tr><td>null</td>             <td>false</td></tr>
     * <tr><td>"", " ", ...</td>     <td>false</td></tr>
     * <tr><td>(everything else)</td><td>true</td></tr>
     * </table>
     */
    public boolean lookupOptionalBool (String key) {
        String param = args.get(key);
//        return param != null && (param.equals("true") || !(param.equals("") || param.equals("false")));
        return StringUtils.isNotBlank(param) && !param.equalsIgnoreCase("false");
    }
    
    /**
     * lookup a necessary parameter of type int
     * @param key the name of the parameter
     * @return the value of the parameter
     * @throws MissingArgumentException if the parameter wasn't given to this job
     */
    public int lookupInt (String key) throws MissingArgumentException {
        if (args.containsKey(key))
            return Integer.parseInt(args.get(key));
        else throw new MissingArgumentException(this, key, "int");
    }
    /** lookup an optional int. If the int isn't set, 0 is returned */
    public int lookupOptionalInt (String key) {
        if (args.containsKey(key)) {
            String val = args.get(key);
            if (StringUtils.isNumeric(val)) {
                return Integer.parseInt(val);
            }
        }
        return 0;
    }

    /** lookup an optional int. Returns defaultValue if it isn't given */
    public int lookupOptionalInt (String key, int defaultValue) {
        if (args.containsKey(key)) {
            String val = args.get(key);
            if (StringUtils.isNumeric(val)) {
                return Integer.parseInt(val);
            }
        }
        return defaultValue;
    }
	
}