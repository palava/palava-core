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

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;


/**
 * abstract, contains some basic features for requesttypes
 * @author Tobias Sarnowski
 */
public class Request {
    
    protected static Charset _charset = Charset.forName("UTF-8");

    public final RequestHeader header;

	private final InputStream _in;
	private long _in_read = 0;

	public Request(RequestHeader header, InputStream in) {
		this._in = new NoCloseInputStream(in);
		this.header = header;
	}

	public InputStream getInputStream() {
		_in_read = header.getContentLength();
		return _in;
	}

	public int read(byte[] data) throws ConnectionLostException, IOException {
		long max_len = header.getContentLength();
		if (max_len - _in_read - data.length < 0)
		{
			throw new IOException("not allowed to read enough bytes, content-length reached");
		}

		int written;
		try
		{
			written = _in.read(data, 0, data.length);
		}
		catch (IOException ioe)
		{
			throw new ConnectionLostException();
		}
		if (written == -1)
		{
			throw new ConnectionLostException();
		}

		_in_read = _in_read + written;
		return written;
	}

	public void freeInputStream() throws ConnectionLostException, IOException {
		byte[] buffer = new byte[1];

		while(_in_read < header.getContentLength())
		{
			read(buffer);
		}
	}


	public static Request formRequest(RequestHeader header, InputStream in) throws ProtocolErrorException
	{
		String type = header.getType();
		
		if (type.equals("data"))
		{
			return new DataRequest(header, in);
		}
		else if (type.equals("text"))
		{
			return new TextRequest(header, in);
		}
		else if (type.equals("json"))
		{
			return new JSONRequest(header, in);
		}
		else if (type.equals("binary"))
		{
			return new BinaryRequest(header, in);
		}
		else
		{
			throw new ProtocolErrorException();
		}
	}
	
	public <R extends Request> R as(Class<R> requestClass) {
	    return requestClass.cast(this);
	}
	
}
