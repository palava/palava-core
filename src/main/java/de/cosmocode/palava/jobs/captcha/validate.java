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

package de.cosmocode.palava.jobs.captcha;

import java.util.Map;

import de.cosmocode.palava.ConnectionLostException;
import de.cosmocode.palava.DataRequest;
import de.cosmocode.palava.Job;
import de.cosmocode.palava.PHPContent;
import de.cosmocode.palava.Request;
import de.cosmocode.palava.Response;
import de.cosmocode.palava.Server;
import de.cosmocode.palava.Session;
import de.cosmocode.palava.components.captcha.Captcha;

public class validate implements Job {

	@Override
	public void process(Request request, Response response, Session session,
			Server server, Map<String, Object> caddy)
			throws ConnectionLostException, Exception {
		
        Captcha captcha = server.components.lookup(Captcha.class);

		DataRequest req = (DataRequest) request;
		Map<?, ?> args = req.getArgs();
		
		String userInput = (String) args.get("code");

		Boolean result = captcha.validate( session.getSessionID(), userInput );
		
		response.setContent( new PHPContent(result));
		


	}

}
