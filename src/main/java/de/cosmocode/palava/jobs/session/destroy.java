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

import org.apache.log4j.Logger;

import de.cosmocode.palava.core.call.Call;
import de.cosmocode.palava.core.protocol.Response;
import de.cosmocode.palava.core.protocol.content.PhpContent;
import de.cosmocode.palava.core.server.Server;
import de.cosmocode.palava.core.session.HttpSession;
import de.cosmocode.palava.legacy.Job;


/**
 * deletes the actual session
 * @author Tobias Sarnowski
 */
public class destroy implements Job
{

    private static final Logger logger = Logger.getLogger( destroy.class ) ;

    public void process( Call request, Response resp, HttpSession session, Server server, Map<String,Object> caddy ) throws Exception
    {
        session.destroy();
        resp.setContent(new PhpContent(PhpContent.OK)) ;
    }
}
