package de.cosmocode.palava;



/**
 * hooks the shutdown event of the virtual machine
 * @author Tobias Sarnowski
 */
public class ShutdownHook implements Runnable
{
	private Server server;

	public ShutdownHook(Server server)
	{
		this.server = server;
	}


	public void run()
	{
		System.out.println("ShutdownHook caught.");
		server.shutdown();
		try {
			server.join();
		} catch(Exception e) {}
	}
}
