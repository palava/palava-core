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

package de.cosmocode.palava.jobs.mail;

import java.util.Map;

import de.cosmocode.palava.ConnectionLostException;
import de.cosmocode.palava.Job;
import de.cosmocode.palava.Server;
import de.cosmocode.palava.components.mail.Mailer;
import de.cosmocode.palava.core.protocol.DataRequest;
import de.cosmocode.palava.core.protocol.PHPContent;
import de.cosmocode.palava.core.protocol.Request;
import de.cosmocode.palava.core.protocol.Response;
import de.cosmocode.palava.core.session.HttpSession;


/**
 * @author Detlef Huettemann
 */
public class send implements Job
{

	public void process(Request request, Response response, HttpSession session, Server server, Map<String,Object> caddy) throws ConnectionLostException, Exception
	{
        Mailer mailer = server.components.lookup(Mailer.class);
        DataRequest req = (DataRequest) request;
        Map<String,Object> args = req.getArgs();

        String template = (String) args.get("template");
        String to = (String) args.get("to");

        mailer.sendMessage(template,"en",args,to);

        response.setContent(new PHPContent("ok"));
	}

}
