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

/** JSONPath - a representation of a (nested) JSON field
 * example: 
 * person
 * person.firstname
 * person[13].firstname
 * orga[30].person[13].firstname
 *
 */
public class JSONPath {
    char [] buf;

    public JSONPath( ) {
        buf = null;
    }
    public JSONPath( char [] buf ) {
        this.buf = buf;
    }
    public String toString() {
        return buf != null ? new String(buf) : "";
    }
    JSONPath( String str ) {
        buf = str.toCharArray();
    }
    public JSONPath field( String field ) {
    	
    	if ( buf != null ) {
    		char [] b = new char[buf.length+1+field.length()];

    		System.arraycopy(buf,0,b,0,buf.length);
    		b[buf.length] = '.';
    		System.arraycopy(field.toCharArray(),0,b,buf.length+1,field.length());

    		return new JSONPath( b );
    	} else {
    		char [] b = new char[field.length()];

    		System.arraycopy(field.toCharArray(),0,b,0,field.length());

    		return new JSONPath( b );
    		
    	}
    }
    public JSONPath index( int idx ) {
        String field = Integer.toString(idx);
        char [] b = new char[buf.length+2+field.length()];

        System.arraycopy(buf,0,b,0,buf.length);
        b[buf.length] = '[';
        System.arraycopy(field.toCharArray(),0,b,buf.length+1,field.length());
        b[b.length-1] = ']';

        return new JSONPath( b );
    }
    
}
