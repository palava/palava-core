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
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;


/**
 * parses the content of a datarequest into a map
 * @author Detlef Hüttemann
 */
public class DataRequest extends Request
{
	private static Logger logger = Logger.getLogger( DataRequest.class ) ;


    private Map<String,String> _args;


	public DataRequest(RequestHeader header, InputStream in)
	{
		super(header, in);
	}

    @Deprecated
    public <K, V> Map<K, V> getArgs() throws ConnectionLostException, IOException {
        if (_args == null) parseArgs();

        @SuppressWarnings("unchecked")
        final Map<K, V> args = (Map<K, V>) _args;
        
        return args;
    }

    public Map<String, String> getArguments() throws ConnectionLostException, IOException {
        if (_args == null) parseArgs();
        return _args;
    }

    private void parseArgs () throws ConnectionLostException, IOException
	{
        if ( _args != null ) throw new IllegalStateException("args already present");

        _args = new HashMap<String,String>();

		// FIXME datenrequest mit einem datensatz von > sizeof(int) gehen verloren!
        byte[] buffer = new byte[(int)header.getContentLength()];

        read(buffer);

        ByteBuffer bb = ByteBuffer.wrap( buffer ) ;
        CharBuffer cb = _charset.decode( bb ) ;

        if ( logger.isDebugEnabled() ) 
            logger.debug(this.header.getJob() + " - Got args:  " + cb.toString() ) ;

        boolean finished = false;
        boolean escaped = false;
        StringBuffer name = new StringBuffer(), value = null;
        while ( !finished ) {
            try {
                char c = cb.get();
                switch ( c ) {
                case '=':
			if (!escaped) {
	                    value = new StringBuffer();
        	            break;
			}
                case '&':
			if (!escaped) {
	                    if ( value != null ) {
        	                _args.put( name.toString(), value.toString() );
                	    }
	                    value = null;
        	            name = new StringBuffer();
                	    break;
			}
                case '\\':
                    if (!escaped) {
                        escaped = true;
			break;
                    } else {
		    	escaped = false;
		    }
                default:
                    if ( value != null ) 
                        value.append(c);
                    else
                        name.append(c);
                }
            } catch ( BufferUnderflowException e ) {
                finished = true;
            }
        }
        if ( value != null )
            _args.put( name.toString(), value.toString() );
    }

}
