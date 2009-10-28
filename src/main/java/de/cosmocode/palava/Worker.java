package de.cosmocode.palava;
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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;


/**
 * lead the input through the processors
 * @author Tobias Sarnowski
 */
public class Worker extends RequestConsumer implements Runnable
{

	private static final Logger logger = Logger.getLogger(Worker.class);
	private static final Logger requestLogger = Logger.getLogger(RequestConsumer.class);

	// the client connection
	private final Server server;
	private Socket client;

	public Worker(Socket client, Server server) {
		this.client = client;
		this.server = server;
	}

	public void run()
	{
		try {
			// initiate the new connection
			InputStream in = client.getInputStream();
			OutputStream out = client.getOutputStream();

			client.setSoTimeout(2000);
			
			Map<String,Object> caddy = new HashMap<String,Object>();
			
			// process all incoming requests
			//while (!server.shutdown_initiated() && client != null)  // FIXME with permanent connections, you should check here for shutdown!
			Benchmark.start();
			while (client != null) {
				Benchmark.hit();
				consumeRequest(server, in, out, caddy);
			}
        } catch (CloseConnection cc) {
        	Benchmark.stop();
            if (Benchmark.isActive()) requestLogger.debug(Benchmark.getCurrent());
		} catch (ConnectionLostException cle) {
			// connection down?
			logger.warn("Worker lost the connection unexpected.");
			cle.printStackTrace();
			return;
		} catch (Exception e) {
			// unknown!
			logger.error("Worker thread died.", e);
			e.printStackTrace();
			return;
		} finally {
			close();
		}
	}
	// close the connection
	private void close()
	{
		try {
			if (client != null) {
				client.close();
			}
		}
		catch (IOException ioe) {}
		client = null;
	}

}

