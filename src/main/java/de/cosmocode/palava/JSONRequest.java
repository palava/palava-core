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
import java.nio.ByteBuffer;
import java.nio.CharBuffer;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * parse the request content as a json object
 * @author Detlef Huettemann
 */
public class JSONRequest extends Request
{
    private String text = null;
    private JSONObject json = null;


	public JSONRequest(RequestHeader header, InputStream in)
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
	public JSONObject getJSONObject() throws ConnectionLostException, IOException, JSONException {
        if ( json == null ) {
            json = new JSONObject(getText());
        }
        return json;
    }

}
