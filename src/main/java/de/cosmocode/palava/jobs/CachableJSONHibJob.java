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

import org.json.JSONException;
import org.json.JSONObject;

import de.cosmocode.palava.MissingArgumentException;
import de.cosmocode.palava.core.call.Call;
import de.cosmocode.palava.core.protocol.JsonCall;
import de.cosmocode.palava.core.protocol.Response;
import de.cosmocode.palava.core.server.Server;
import de.cosmocode.palava.core.session.HttpSession;
import de.cosmocode.palava.jobs.hib.CachableHibJob;

public abstract class CachableJSONHibJob extends CachableHibJob {
	
	private JSONObject json;

	@Override
	public final void process(Call request, Response response, HttpSession s, Server server, 
		Map<String, Object> caddy, org.hibernate.Session session) throws Exception {

		JsonCall jRequest = (JsonCall) request;
		json = jRequest.getJSONObject();
		
		if (session == null) session = createHibSession(server, caddy);

		process(json, response, s, server, caddy, session);
		session.flush();
	}
    
    protected abstract void process(JSONObject json, Response response, HttpSession s, Server server,
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