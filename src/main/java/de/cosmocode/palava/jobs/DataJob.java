/**
 * palava - a java-php-bridge
 * Copyright (C) 2007  CosmoCode GmbH
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package de.cosmocode.palava.jobs;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

import de.cosmocode.palava.Job;
import de.cosmocode.palava.MissingArgumentException;
import de.cosmocode.palava.core.call.Call;
import de.cosmocode.palava.core.protocol.ConnectionLostException;
import de.cosmocode.palava.core.protocol.DataCall;
import de.cosmocode.palava.core.protocol.Response;
import de.cosmocode.palava.core.server.Server;
import de.cosmocode.palava.core.session.HttpSession;

public abstract class DataJob implements Job {
    
    private Map<String, String> args;

    @Override
    @SuppressWarnings("unchecked")
    public final void process(Call request, Response response, HttpSession session, Server server, 
        Map<String, Object> caddy) throws ConnectionLostException, Exception {
        
        DataCall dataRequest = (DataCall) request;
        args = dataRequest.getArgs();
        
        process(args, response, session, server, caddy);
    }
    
    protected final void validate(String... keys) throws MissingArgumentException {
        for (String key : keys) {
            if (!args.containsKey(key)) throw new MissingArgumentException(key);
        }
    }
    
    protected abstract void process(Map<String, String> args, Response response, HttpSession session, Server server, 
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