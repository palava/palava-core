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

import org.json.JSONObject;

import de.cosmocode.palava.MissingArgumentException;
import de.cosmocode.palava.Server;
import de.cosmocode.palava.core.protocol.JSONRequest;
import de.cosmocode.palava.core.protocol.Request;
import de.cosmocode.palava.core.protocol.Response;
import de.cosmocode.palava.core.session.HttpSession;
import de.cosmocode.palava.jobs.hib.HibJob;

public abstract class JSONHibJob extends HibJob {
	
	private JSONObject json;

	@Override
	public final void process(Request request, Response response, HttpSession s, Server server, 
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
	
	protected abstract void process(JSONObject json, Response response, HttpSession s, Server server,
		Map<String, Object> caddy, org.hibernate.Session session) throws Exception;

}