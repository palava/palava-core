/**
 * palava - a java-php-bridge
 * Copyright (C) 2007-2010  CosmoCode GmbH
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

package de.cosmocode.palava.bridge.simple.content;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.extension.JSONConstructor;
import org.json.extension.JSONEncoder;

import de.cosmocode.json.JSON;
import de.cosmocode.json.JSONMapable;
import de.cosmocode.json.JSONRenderer;
import de.cosmocode.patterns.Immutable;

/**
 * use the JSONConverter to produce JSON output of java objects.
 * 
 * @author Detlef Hüttemann
 * @author Willi Schoenborn
 */
@Immutable
public class JsonContent extends AbstractContent {

    public static final JsonContent EMPTY = new JsonContent(new JSONObject());
    
    private static final byte[] NULL = "null".getBytes();
    
    private final byte[] bytes;
    
    public JsonContent(JSONRenderer renderer) {
        super(MimeType.JSON);
        bytes = renderer == null ? NULL : renderer.toString().getBytes();
    }
    
    public JsonContent(JSONObject object) {
        super(MimeType.JSON);
        bytes = object == null ? NULL : object.toString().getBytes();
    }
    
    public JsonContent(JSONArray array) {
        super(MimeType.JSON);
        bytes = array == null ? NULL : array.toString().getBytes();
    }
    
    public JsonContent(JSONConstructor constructor) {
        super(MimeType.JSON);
        bytes = constructor == null ? NULL : constructor.toString().getBytes();
    }
    
    public JsonContent(JSONMapable mapable) {
        super(MimeType.JSON);
        bytes = mapable == null ? NULL : JSON.createJSONRenderer().object(mapable).toString().getBytes();
    }
    
    public JsonContent(JSONEncoder encoder) {
        super(MimeType.JSON);
        bytes = encoder == null ? NULL : JSON.createJSONRenderer().object(encoder).toString().getBytes();
    }
    
    public <E> JsonContent(Iterable<E> iterable) {
        super(MimeType.JSON);
        bytes = iterable == null ? NULL : JSON.createJSONRenderer().array(iterable).toString().getBytes();
    }
    
    public <K, V> JsonContent(Map<K, V> map) {
        super(MimeType.JSON);
        bytes = map == null ? NULL : JSON.createJSONRenderer().object(map).toString().getBytes();
    }
    
    @Override
    public long getLength() {
        return bytes.length;
    }
    
    @Override
    public void write(OutputStream out) throws IOException {
        out.write(bytes, 0, bytes.length);
    }
    
}
