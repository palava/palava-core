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
