package de.cosmocode.palava.jobs.session;
/*
palava - a java-php-bridge
Copyright (C) 2007  CosmoCode GmbH

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/

import java.util.Map;

import org.apache.log4j.Logger;

import de.cosmocode.palava.Benchmark;
import de.cosmocode.palava.ClientData;
import de.cosmocode.palava.DataRequest;
import de.cosmocode.palava.Job;
import de.cosmocode.palava.PHPContent;
import de.cosmocode.palava.Request;
import de.cosmocode.palava.Response;
import de.cosmocode.palava.Server;
import de.cosmocode.palava.Session;


/**
 * checks session and creates new if necessary. 
 * requires datarequest. return phpcontent(sessionid)
 * @author Detlef Hüttemann
 */
public class initialize implements Job {

    private static final Logger logger = Logger.getLogger( initialize.class ) ;

	public void process( Request request, Response response, Session session, Server server, 
            Map<String,Object> caddy ) throws Exception {
        
        DataRequest dRequest = (DataRequest) request;
        Map<String, String> args = dRequest.getArgs();
        
        if (args.containsKey("url")) {
            String url = args.get("url");
            Benchmark.setURL(url);
        }
        
        if ( session != null ) {

            ClientData clientData = session.getClientData() ;

            if ( clientData == null ) {
                logger.error("no clientData");
                session = null;
            } else {
                if (!clientData.isValid(args)) {
                    logger.info("sessions client data differs");
                    session = null;
                }
            }
        }

        if ( session == null ) {
            session = server.sessions.createSession();
            session.setClientData(new ClientData(args));
        }
        
        // send current sessionid as response
        //
        //

        response.setContent( new PHPContent( session.getSessionID() ) ) ;

    }
}
