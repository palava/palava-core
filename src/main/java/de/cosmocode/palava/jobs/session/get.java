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

package de.cosmocode.palava.jobs.session;

import java.util.Map;

import de.cosmocode.palava.ConnectionLostException;
import de.cosmocode.palava.Job;
import de.cosmocode.palava.Server;
import de.cosmocode.palava.core.protocol.DataRequest;
import de.cosmocode.palava.core.protocol.PHPContent;
import de.cosmocode.palava.core.protocol.Call;
import de.cosmocode.palava.core.protocol.Response;
import de.cosmocode.palava.core.session.HttpSession;


/**
 * get a session data entry
 * @author Detlef Hüttemann
 */
public class get implements Job
{

	public void process(Call request, Response response, HttpSession session, Server server, Map<String,Object> caddy) throws ConnectionLostException, Exception
	{
        DataRequest req = (DataRequest) request;
        Map<String, String> args = req.getArgs();
        String key = args.get("key");
        if ( key == null ) throw new NullPointerException("key");
        Object data = session.get( key ) ;
        if ( data != null )
            response.setContent(new PHPContent(data));
        else 
            response.setContent( PHPContent.NOT_FOUND );
	}

}
