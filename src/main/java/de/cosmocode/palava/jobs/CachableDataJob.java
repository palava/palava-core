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