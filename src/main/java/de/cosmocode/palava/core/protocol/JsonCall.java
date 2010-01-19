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

package de.cosmocode.palava.core.protocol;

import java.io.IOException;
import java.io.InputStream;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * parse the request content as a json object.
 * 
 * @author Detlef Huettemann
 */
public class JsonCall extends TextCall {
    
    private JSONObject json;

    public JsonCall(Header header, InputStream in) {
        super(header, in);
    }

    public JSONObject getJSONObject() throws ConnectionLostException, IOException, JSONException {
        if (json == null) {
            json = new JSONObject(getText());
        }
        return json;
    }

}
