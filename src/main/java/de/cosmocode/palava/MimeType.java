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


/**
 * a mimetype and some static mimetypes
 * @author Detlef Hüttemann
 */
public class MimeType
{
    private String _type;
    private int _slash;

	public static final MimeType Error = new MimeType("application/error");
    public static final MimeType PHP = new MimeType("application/x-httpd-php");
    public static final MimeType JSON = new MimeType("application/json");
	public static final MimeType Text = new MimeType("text/plain");
	public static final MimeType XML = new MimeType("application/xml");
	public static final MimeType HTML = new MimeType("text/html");
	public static final MimeType Image = new MimeType("image/*");
	public static final MimeType Jpeg = new MimeType("image/jpeg");
	

    public MimeType( String type ) {
        if ( type == null ) throw new NullPointerException("MimeType");
        _type = type;
        _slash = _type.indexOf("/");
        if ( _slash <0 || _slash == _type.length()-1 )
        	throw new IllegalArgumentException("missing '/' in mimetype declaration");
    }

    public String toString() {
        return _type;
    }
    public boolean equals( Object o ) {
        return (o instanceof MimeType) && _type.equals(((MimeType)o)._type);
    }

    /** returns the part after the '/'
     */
	public String getMinor() {
		
		return _type.substring(_slash+1);
	}

}
