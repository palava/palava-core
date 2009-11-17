package de.cosmocode.palava;

import java.io.BufferedOutputStream;
import java.io.OutputStream;

import org.apache.log4j.Logger;


/**
 * sends a content object to the palava frontend
 * @author Tobias Sarnowski
 */
public class Response
{

	private static final Logger logger = Logger.getLogger(Response.class) ;

	private OutputStream out;

	private Content content = null;
	

	private boolean _already_sent = false;



	public Response(OutputStream out)
	{
		this.out = out;
	}


	public OutputStream getOutputStream()
	{
		return out;
	}


    public boolean hasContent() 
    {
        return content != null;
    }
	public void setContent(Content content)
	{
		this.content = content;
	}
	public Content getContent()
	{
		return content;
	}

	public boolean contentSet()
	{
		return (content != null);
	}

	
	public void send() throws Exception
	{
		BufferedOutputStream bout = new BufferedOutputStream( out ) ;
		if (_already_sent)
		{
			throw new IllegalStateException("already sent");
		}
        if ( content == null ) 
            throw new NullPointerException("content");

		// header
		String header = content.getMimeType() + "://(" + content.getLength() + ")?";

        logger.debug("Response:  " + content.getMimeType() + " [" + content.getLength() + " bytes]");

		bout.write(header.getBytes());

		// body
        content.write(bout) ;
        bout.flush();

		_already_sent = true;
	}

	public boolean alreadySent()
	{
		return _already_sent;
	}

}
