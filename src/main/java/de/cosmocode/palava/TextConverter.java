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
 * convert java objects into plain text
 * @author Detlef Hüttemann
 */
public class TextConverter extends ContentConverter
{

    public void convertMap( StringBuffer buf, Map map ) throws ConversionException {
        Iterator<Map.Entry> iter = map.entrySet().iterator();
        boolean hasNext = iter.hasNext();
        while( hasNext ) {
            Map.Entry me = iter.next();

            Object key = me.getKey();
            buf.append( key.toString() ).append( " => " );
            convert( buf, me.getValue() ) ;
            hasNext = iter.hasNext();
            if ( hasNext ) 
                buf.append("\n");
        }
        buf.append(")");
    }


    public void convertNull( StringBuffer buf ) throws ConversionException {
        buf.append("null");
    }

    public void convertString( StringBuffer buf, String object ) throws ConversionException {
        buf.append( object );
    }

    public void convertDate( StringBuffer buf, java.util.Date object ) throws ConversionException {
        buf.append( object.getTime()/1000 );
    }
    public void convertNumber( StringBuffer buf, Number object ) throws ConversionException {
        buf.append( object );
    }
    public void convertList( StringBuffer buf, List object ) throws ConversionException {
        Iterator iter = object.iterator();
        boolean hasNext = iter.hasNext();
        while( hasNext ) {
            Object key = iter.next();
            convert( buf, key ) ;
            hasNext = iter.hasNext();
            if ( hasNext ) 
                buf.append("\n");
        }
    }
    public void convertDefault( StringBuffer buf, Object object ) throws ConversionException {
        convertString( buf, object.toString() );
    }
   public void convertKeyValue( StringBuffer buf, String key, Object value, KeyValueState state ) throws ConversionException {
    }

}
