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

import com.google.inject.Inject;

import de.cosmocode.palava.Job;
import de.cosmocode.palava.core.call.Call;
import de.cosmocode.palava.core.protocol.Response;
import de.cosmocode.palava.core.protocol.content.TextContent;
import de.cosmocode.palava.core.server.Server;
import de.cosmocode.palava.core.session.HttpSession;
import de.cosmocode.palava.core.session.HttpSessionManager;


/**
 * checks session and creates new if necessary. 
 * requires datarequest. return phpcontent(sessionid)
 * @author Detlef Hüttemann
 */
public class initialize implements Job {

    @Inject
    private HttpSessionManager sessionManager;

    @Override
    public void process(Call call, Response response, HttpSession session, Server server, 
        Map<String, Object> caddy) throws Exception {
        
        final HttpSession current;
        
        if (session == null) {
            current = sessionManager.get();
        } else {
            current = session;
        }
        
        response.setContent(new TextContent(current.getSessionId()));

    }
}
