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

import java.io.InputStream;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.common.base.Functions;
import com.google.common.collect.Maps;

import de.cosmocode.collections.utility.UtilityMap;
import de.cosmocode.json.JSON;
import de.cosmocode.palava.core.command.Command;
import de.cosmocode.palava.core.request.HttpRequest;

/**
 * parse the request content as a json object.
 * 
 * @author Detlef Huettemann
 */
public class JsonCall extends TextCall {
    
    private JSONObject json;
    private UtilityMap<String, Object> map;

    JsonCall(HttpRequest request, Command command, Header header, InputStream stream) {
        super(request, command, header, stream);
    }

    public final JSONObject getJSONObject() throws ConnectionLostException, JSONException {
        if (json == null) {
            json = new JSONObject(getText());
        }
        return json;
    }
    
    @Deprecated
    public final <K, V> UtilityMap<K, V> getArgs() {
        if (map == null) {
            try {
                map = JSON.asMap(getJSONObject());
            } catch (JSONException e) {
                throw new IllegalArgumentException(e);
            }
        }
        return (UtilityMap<K, V>) map;
    }
    
    public final UtilityMap<String, Object> getArguments() {
        if (map == null) {
            try {
                map = JSON.asMap(getJSONObject());
            } catch (JSONException e) {
                throw new IllegalArgumentException(e);
            }
        }
        return map;
    }

    public final Map<String, String> getStringedArguments() throws ConnectionLostException {
        return Maps.transformValues(getArguments(), Functions.toStringFunction());
    }

}
