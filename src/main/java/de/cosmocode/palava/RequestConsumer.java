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

package de.cosmocode.palava;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.cosmocode.palava.core.call.Call;
import de.cosmocode.palava.core.protocol.DefaultResponse;
import de.cosmocode.palava.core.protocol.ErrorContent;
import de.cosmocode.palava.core.protocol.Response;
import de.cosmocode.palava.core.session.HttpSession;

public class RequestConsumer {

	private static final Logger log = LoggerFactory.getLogger(RequestConsumer.class);
	
	public static final ThreadLocal<HttpSession> SESSION = new ThreadLocal<HttpSession>();

	void consumeRequest(Server server, InputStream in, OutputStream out, Map<String, Object> caddy)
		throws ConnectionLostException, CloseConnection, Exception {
	    
		// read the request
		final RequestHeader header = RequestHeader.createHeader(in);
	
		final Call request = header.getType().createRequest(header, in);
	
		// find the session
	    final String sessionID = header.getSessionID();
	    HttpSession session = null;
	
	    if (sessionID.length() > 0)
		    session = server.sessions.get(header.getSessionID());
	
		if (session != null) {
            session.updateAccessTime();
		}
	
		// prepare the response object
		// TODO use injector
		final Response response = new DefaultResponse(out);
	
		// initialize the job
		Job job = null;
		boolean process = true;
	
		final String jobname = request.getHeader().getJob();
		
		try {
			job = server.jobs.getJob(jobname);
		} catch (Exception ex) {
			log.error("Job " + jobname + " not found!", ex);
			createErrorResponse(response, ex);
			process = false;
		}
	
		// let the job do his things
		try {
			if (process) {
			    
			    for (JobInterceptor interceptor : server.getJobInterceptors()) {
			        log.debug("Running interceptor {} on {}", interceptor, job);
			        interceptor.intercept(server.components, job);
			    }
			    
			    SESSION.set(session);
				job.process(request, response, session, server, caddy);
			}
		} catch (CloseConnection e) {
			throw e;
		} catch (ConnectionLostException e) {
			log.warn("Connection lost during job processing.");
			if (caddy != null) {
			    for (Object value : caddy.values()) {
		            if (value instanceof Closable) {
		                Closable.class.cast(value).onClose();
		            }
		        }
			}
			return;
		} catch (Exception e) {
			log.error("Job " + jobname + " died with an error.", e);
			createErrorResponse(response, e);
		}
	
		request.freeInputStream();
	
        if (!response.hasContent())
			createErrorResponse(response, new NullPointerException("no content in job " + jobname));

		if (!response.sent()) {
	        response.send();
		}
	}
	
	private void createErrorResponse(Response response, Exception e) {
        response.setContent(new ErrorContent(e));
	}

}
