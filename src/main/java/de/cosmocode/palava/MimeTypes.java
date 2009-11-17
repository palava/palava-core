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

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;


/**
 * can parse the mimetype from a file extension
 * @author Detlef Hüttemann
 */
public class MimeTypes
{

	public static MimeTypes SINGLETON;
    
    static {
        try {
            SINGLETON = new MimeTypes("/etc/mime.types");
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    };

	private Map<String,MimeType> _mimes;

    private MimeTypes( String file ) throws Exception {
        _mimes = new HashMap<String,MimeType>();
        BufferedReader reader = new BufferedReader( new FileReader( file ) ) ;
        String line;
        Pattern pattern = Pattern.compile("\\s+");


        while ( null != ( line = reader.readLine())) {
            if ( line.length() > 0 && line.charAt(0) != '#' ) {
                String [] result = pattern.split( line ) ;
                if ( result.length > 1 ) {
                    MimeType mime = new MimeType(result[0]);
                    for ( int i=1;i<result.length;i++) {
                        _mimes.put( result[i], mime );
                    }
                }
            }
        }
    }

	public MimeType getMimeTypeByExt( String ext ) {
		return _mimes.get( ext ) ;
	}
	public MimeType getMimeTypeByName( String name ) {
        int dot = name.lastIndexOf(".");
        if ( dot != -1 ) 
		    return getMimeTypeByExt( name.substring(dot+1).toLowerCase());
        return null;
	}

}
