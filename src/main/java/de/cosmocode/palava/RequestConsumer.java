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

public class RequestConsumer {

	private static final Logger log = LoggerFactory.getLogger(RequestConsumer.class);
	
	public static final ThreadLocal<Session> SESSION = new ThreadLocal<Session>();

	void consumeRequest(Server server, InputStream in, OutputStream out, Map<String, Object> caddy)
			throws ConnectionLostException, CloseConnection, Exception {
	    
		// read the request
		RequestHeader header = RequestHeader.formHeader(in);
	
		Request request = Request.formRequest(header, in);
	
		// find the session
	    String sessionID = header.getSessionID();
	    Session session = null;
	
	    if ( sessionID.length() > 0 )
		    session = server.sessions.get(header.getSessionID());
	
		if (session != null) {
            session.updateAccessTime();
		}
	
		// prepare the response object
		Response response = new Response(out);
	
		// initialize the job
		Job job = null;
		boolean process = true;
	
		String jobname = request.header.getJob();
		
		Server.startBench(0);
	
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
            log(jobname, e);
			log.error("Job " + jobname + " died with an error.", e);
			createErrorResponse(response, e);
		} finally {
			log(jobname);
		}
	
		request.freeInputStream();
	
        if ( !response.hasContent() )
			createErrorResponse(response, new NullPointerException("no content in job " + jobname) );

		if (!response.alreadySent()) {
			Server.startBench(1);
	        response.send();
		}
	}
	
	private void log(String job) {
	    log(job, null);
	}
	
	private void log(String job, Exception e) {
	    
	    final StringBuilder message = new StringBuilder();
	    message.
	        append("Req: ").
	        append(job).append(" ").
	        append(Server.getBench(0)).append(" ms ").
	        append(Thread.currentThread().getName());
	    
	    if (e != null) {
	        message.append(" (").append(e.getMessage()).append(")");
	    }
	    
	    log.debug(message.toString());
	}

	private void createErrorResponse(Response response, Exception e) {
		try {
			response.setContent(new ErrorContent(e));
		} catch (Exception ex) {
			log.error("Cannot set error response!", ex);
		}
	}

}
