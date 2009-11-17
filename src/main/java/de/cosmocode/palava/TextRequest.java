package de.cosmocode.palava;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;


/**
 * parse the request content as one big plain text
 * @author Tobias Sarnowski
 */
public class TextRequest extends Request
{
    private String text = null;


	public TextRequest(RequestHeader header, InputStream in)
	{
		super(header, in);
	}


	public String getText() throws ConnectionLostException, IOException
	{
		if (text == null)
		{
			byte[] buffer = new byte[(int)header.getContentLength()];
			read(buffer);
			ByteBuffer bb = ByteBuffer.wrap(buffer);
			CharBuffer cb = _charset.decode(bb) ;
			text = cb.toString();
		}
		return text;
	}

}
