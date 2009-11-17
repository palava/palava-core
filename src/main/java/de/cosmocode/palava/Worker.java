package de.cosmocode.palava;

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

