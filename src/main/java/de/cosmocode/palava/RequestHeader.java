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


/**
 * parse an inputstream for a palava protocol header and represent it as an object
 * @author Tobias Sarnowski
 */
public class RequestHeader
{


	private String _type;
	private String _job;
	private String _session_id;
	private long _content_length;


	public RequestHeader(String type, String job, String session_id, long content_length)
	{
		this._type = type;
		this._job = job;
		this._session_id = session_id;
		this._content_length = content_length;
	}


	public String getType()
	{
		return _type;
	}

	public String getJob()
	{
		return _job;
	}

	public String getSessionID()
	{
		return _session_id;
	}

	public long getContentLength()
	{
		return _content_length;
	}



	public static RequestHeader formHeader(InputStream in) throws ProtocolErrorException, ConnectionLostException
	{
		boolean header_complete = false;
		int part = 0;

		StringBuffer type = new StringBuffer();
		StringBuffer job = new StringBuffer();
		StringBuffer session_id = new StringBuffer();
		StringBuffer content_length = new StringBuffer();

		while (!header_complete)
		{
			// read one byte
			int buffer = 0;
			try
			{
				buffer = in.read();
			}
			catch (IOException ioe)
			{
				throw new ConnectionLostException();
			}
			if (buffer == -1)
			{
				throw new ConnectionLostException();
			}

			// parse the byte
			switch (part)
			{
				case 0:  //  type :
						if ((char)buffer == ':')
						{
							part++;
						}
						else
						{
							type.append((char)buffer);
						}
						break;

				case 1:  //  /
				case 2:
						if ((char)buffer == '/')
						{
							part++;
						}
						else
						{
							throw new ProtocolErrorException();
						}
						break;

				case 3:  //  job /
						if ((char)buffer == '/')
						{
							part++;
						}
						else
						{
							job.append((char)buffer);
						}
						break;

				case 4:  //  session_id /
						if ((char)buffer == '/')
						{
							part++;
						}
						else
						{
							session_id.append((char)buffer);
						}
						break;

				case 5:  //  (
						if ((char)buffer == '(')
						{
							part++;
						}
						else
						{
							throw new ProtocolErrorException();
						}
						break;

				case 6:  //  content-length )
						if ((char)buffer == ')')
						{
							part++;
						}
						else
						{
							content_length.append((char)buffer);
						}
						break;

				case 7:  //  ?
						if ((char)buffer == '?')
						{
							header_complete = true;
						}
						else
						{
							throw new ProtocolErrorException();
						}
						break;
						
			}
		}

//		log.info("Request:  " + job.toString());
//		log.debug("Request:  <" + type.toString() + "> " + job.toString() + " (SID: " + session_id.toString() + ") [" + content_length.toString() + " bytes]");
		return new RequestHeader(type.toString(), job.toString(), session_id.toString(), Long.parseLong(content_length.toString()));
	}

}
