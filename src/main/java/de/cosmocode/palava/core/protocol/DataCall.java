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
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.Map;

import org.apache.log4j.Logger;

import com.google.common.base.Preconditions;
import com.google.inject.internal.Maps;

/**
 * parses the content of a datarequest into a map.
 * 
 * @author Detlef Hüttemann
 * @author Willi Schoenborn
 * @deprecated use {@link JsonCall} instead
 */
@Deprecated
public final class DataCall extends AbstractCall {
    
    private static final Logger log = Logger.getLogger(DataCall.class);

    private Map<String, String> arguments;

    public DataCall(Header header, InputStream stream) {
        super(header, stream);
    }

    /**
     * 
     * 
     * @param <K>
     * @param <V>
     * @return
     * @throws ConnectionLostException
     * @throws IOException
     * @deprecated use {@link DataCall#getArguments()} instead
     */
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

        arguments = Maps.newHashMap();

        final byte[] buffer = new byte[(int) getHeader().getContentLength()];

        try {
            read(buffer);
        } catch (IOException e) {
            throw new ConnectionLostException(e);
        }

        final ByteBuffer bb = ByteBuffer.wrap(buffer);
        final CharBuffer cb = CHARSET.decode(bb);

        boolean finished = false;
        boolean escaped = false;
        StringBuilder name = new StringBuilder();
        StringBuilder value = new StringBuilder();
        
        while (true) {
            if (finished) break;
            if (!cb.hasRemaining()) {
                finished = true;
            }
            final char c;
            try {
                c = cb.get();
            } catch (BufferUnderflowException e) {
                break;
            }
            switch (c) {
                case '=': {
                    if (!escaped) {
                        value = new StringBuilder();
                        break;
                    }
                }
                case '&': {
                    if (!escaped) {
                        if (value != null) {
                            arguments.put(name.toString(), value.toString());
                        }
                        value = null;
                        name = new StringBuilder();
                        break;
                    }
                }
                case '\\': {
                    if (!escaped) {
                        escaped = true;
                        break;
                    } else {
                        escaped = false;
                    }
                }
                default: {
                    if (value == null) {
                        name.append(c);
                    } else {
                        value.append(c);
                    }
                }
            }
        }
        
        if (value != null) {
            arguments.put(name.toString(), value.toString());
        }
    }

}
