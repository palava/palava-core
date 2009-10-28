package de.cosmocode.palava;
/*
palava - a java-php-bridge
Copyright (C) 2007  CosmoCode GmbH

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;
import org.json.extension.JSONConstructor;
import org.json.extension.JSONEncoder;

/**
 * use the JSONConverter to produce JSON output of java objects
 * @author Detlef Hüttemann
 */
public class JSONContent extends Content
{

    public static final JSONContent EMPTY;
    
    static {
        try {
            EMPTY = new JSONContent(new JSONObject());
        } catch (ConversionException e) {
            throw new RuntimeException(e);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
    
    byte [] _bytes;
    
    public JSONContent( Object object ) throws ConversionException, JSONException {
    	
        if ( object == null ) {
            _bytes = "null".getBytes();
        } else if ( object instanceof JSONObject 
        		 || object instanceof JSONArray
        		 || object instanceof JSONConstructor) {
    		_bytes = object.toString().getBytes();
        } else if ( object instanceof JSONEncoder ) {
            JSONStringer builder = new JSONStringer();
            ((JSONEncoder)object).encodeJSON(builder);
    		_bytes = builder.toString().getBytes();
    	} else if (object instanceof Iterable<?>) {
    		Iterable<?> list = (Iterable<?>) object;
    	    JSONConstructor json = new JSONStringer();    	    
    	    json.array();    	    
    	    for (Object e : list) {
    	        if (e instanceof JSONEncoder) {
    	            ((JSONEncoder) e).encodeJSON(json);
    	        } else {
    	            json.value(e);
    	        }
    	    }    	    
    	    json.endArray();    	    
    	    _bytes = json.toString().getBytes();
    	} else if (object instanceof Map<?, ?>) {
    	    Map<?, ?> map = (Map<?, ?>) object;
    	    JSONConstructor json = new JSONStringer();    	    
    	    json.object();
    	    for (Map.Entry<?, ?> entry : map.entrySet()) {
    	        json.key(entry.getKey().toString());
    	        if (entry.getValue() instanceof JSONEncoder) {
    	            ((JSONEncoder) entry.getValue()).encodeJSON(json);    	            
    	        } else {
    	            json.value(entry.getValue());
    	        }
    	    }
    	    json.endObject();    	    
    	    _bytes = json.toString().getBytes();
    	} else {
    		JSONConverter converter = new JSONConverter () ;
    		StringBuffer buf = new StringBuffer () ;
    		converter.convert( buf, object ) ;
            _bytes = buf.toString().getBytes();
    	}
    	
        _length = _bytes.length;
        _mime = MimeType.JSON;
    }
    
    public void write( OutputStream out ) throws IOException {
        out.write( _bytes, 0, (int)_length );
    }
}