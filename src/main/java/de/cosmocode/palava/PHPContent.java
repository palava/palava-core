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

import org.apache.log4j.Logger;


/**
 * use the PHPConverter to produce php output of java objects
 * @author Detlef Hüttemann
 */
@Deprecated
public class PHPContent extends Content
{
    private static final Logger logger = Logger.getLogger( PHPContent.class ) ;

    public static PHPContent OK = null;
    public static PHPContent NOT_FOUND = null;
    
    static {
        try {
            OK = new PHPContent("ok") ;
            NOT_FOUND = new PHPContent("not_found") ;
        } catch (ConversionException e ) {
            logger.error("cannot create default objects", e );
        }
    };

    byte [] _bytes;
    public PHPContent( Object object ) throws ConversionException {
        PHPConverter converter = new PHPConverter () ;
        StringBuffer buf = new StringBuffer () ;
        converter.convert( buf, object ) ;
        _bytes = buf.toString().getBytes();
        _length = _bytes.length;
        _mime = MimeType.PHP;
    }
    public void write( OutputStream out ) throws IOException {
        out.write( _bytes, 0, (int)_length );
    }
}


