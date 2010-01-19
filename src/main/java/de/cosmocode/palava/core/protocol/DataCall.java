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
import java.util.Map;

import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Functions;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

import de.cosmocode.json.JSON;

/**
 * parses the content of a datarequest into a map.
 * 
 * @author Detlef Hüttemann
 * @author Willi Schoenborn
 * @deprecated use {@link JsonCall} instead
 */
@Deprecated
public final class DataCall extends JsonCall {
    
    private static final Logger log = LoggerFactory.getLogger(DataCall.class);

    private Map<String, String> arguments;

    public DataCall(Header header, InputStream stream) {
        super(header, stream);
    }

    @Deprecated
    public <K, V> Map<K, V> getArgs() throws ConnectionLostException, IOException {
        if (arguments == null) parseArgs();

        @SuppressWarnings("unchecked")
        final Map<K, V> args = (Map<K, V>) arguments;
        
        return args;
    }

    public Map<String, String> getArguments() throws ConnectionLostException {
        if (arguments == null) parseArgs();
        return arguments;
    }

    private void parseArgs() throws ConnectionLostException {
        Preconditions.checkState(arguments == null, "Arguments already parsed");
        try {
            arguments = Maps.transformValues(JSON.asMap(getJSONObject()), Functions.toStringFunction());
        } catch (IOException e) {
            throw new IllegalStateException(e);
        } catch (JSONException e) {
            throw new IllegalStateException(e);
        }
        log.debug("Parsed arguments: {}", arguments);
    }

}
