package de.cosmocode.palava;

import java.io.IOException;
import java.io.InputStream;


/**
 * wraps an InputStream and disables the close() command
 * @author Tobias Sarnowski
 */
public class NoCloseInputStream extends InputStream
{
	private InputStream in;

	public NoCloseInputStream(InputStream in)
	{
		this.in = in;
	}

	public void close() { /* do nothing! */ }

	public int read() throws IOException
	{
		return in.read();
	}
}
