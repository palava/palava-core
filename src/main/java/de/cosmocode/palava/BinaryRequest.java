package de.cosmocode.palava;

import java.io.InputStream;


/**
 * use this for stream transfer
 * @author Tobias Sarnowski
 */
public class BinaryRequest extends Request
{
	public BinaryRequest(RequestHeader header, InputStream in)
	{
		super(header, in);
	}

}
