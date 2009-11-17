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

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Converts a java object into json.
 * 
 * @author Detlef Huettemann
 */
public class JSONConverter extends ContentConverter {

    public void convertMap( StringBuffer buf, Map map ) throws ConversionException {
        buf.append("{");
        Iterator<Map.Entry> iter = map.entrySet().iterator();
        boolean hasNext = iter.hasNext();
		if (!hasNext)
		{
			buf.append("}");
			return;
		}
        while( hasNext ) {
            Map.Entry me = iter.next();

            Object key = me.getKey();
            Object value = me.getValue();
            hasNext = iter.hasNext();

            convertKeyValue( buf, key.toString(), value, hasNext ? KeyValueState.INSIDE : KeyValueState.LAST ) ;
        }
    }


    public void convertNull( StringBuffer buf ) throws ConversionException {
    }
    public void convertString( StringBuffer buf, String object ) throws ConversionException {
		String buffer = object.toString().replace("\\", "\\\\");
		buffer = buffer.replace("\"", "\\\"");
        buf.append("\"").append( buffer ).append("\"");
    }
    public void convertDate( StringBuffer buf, java.util.Date object ) throws ConversionException {
        buf.append( object.getTime()/1000 );
    }
    public void convertNumber( StringBuffer buf, Number object ) throws ConversionException {
        buf.append( object );
    }
    public void convertList( StringBuffer buf, List object ) throws ConversionException {
        buf.append("[");
        Iterator iter = object.iterator();
        boolean hasNext = iter.hasNext();
        while( hasNext ) {
            Object key = iter.next();
            convert( buf, key ) ;
            hasNext = iter.hasNext();
            if ( hasNext ) 
                buf.append(",");
        }
        buf.append("]");
    }
    public void convertDefault( StringBuffer buf, Object object ) throws ConversionException {
        convertString( buf, object.toString() );
    }

   public void convertKeyValue( StringBuffer buf, String key, Object value, KeyValueState state ) throws ConversionException {
    	if ( state == KeyValueState.ZERO) {
    		buf.append("{}");
    		return;
    	}
    	else if ( state == KeyValueState.SINGLE || state == KeyValueState.START ) buf.append("{");

	convert(buf, key);
    	buf.append(":");
    	convert(buf, value);

    	if ( state != KeyValueState.LAST ) buf.append(",");
    	if ( state == KeyValueState.LAST || state == KeyValueState.SINGLE) buf.append("}");
   }
}
