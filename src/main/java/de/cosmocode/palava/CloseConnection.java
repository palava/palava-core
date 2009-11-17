package de.cosmocode.palava;


/**
 * Not a real exception, thrown by the close-job.
 * Indicates, that the server should close the connection
 * without an error.
 * @author Tobias Sarnowski
 */
public class CloseConnection extends Exception {
	
	private static final long serialVersionUID = 6642833065438659444L;
	
	private static final CloseConnection instance = new CloseConnection();
	
	private CloseConnection() {
		
	}
	
	public static CloseConnection getInstance() {
		return instance;
	}
	
}
