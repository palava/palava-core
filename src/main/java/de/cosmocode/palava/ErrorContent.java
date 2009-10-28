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
import java.io.PrintWriter;
import java.io.StringWriter;


/**
 * an error occured, this ships it to the browser
 * @author Tobias Sarnowski
 */
public class ErrorContent extends Content
{
    byte [] _bytes;

    public ErrorContent(Exception e) throws ConversionException
	{
		// FIXME use a real ErrorObject for transaction
		//       php is good enough to create the html itself, it's ugly here
        StringBuffer buf = new StringBuffer();

		buf.append("<div style='background-color: #ff0000 !important; margin: 20px; font-family: Lucida Grande, Verdana, Sans-serif; font-size: 12px; color: #ffffff !important; border-color: #000000 !important; border-width: 2px; border-style: solid; padding: 10px; text-align: left'>");
		buf.append("<span style='font-weight: bold; font-size: 16px; background-color: #ff0000 !important'>Palava Error:  ");
		buf.append(htmlspecialchars(e.toString()));
		buf.append("</span>");

		buf.append("<pre style='background-color: #ff0000 !important'>");
		StringWriter esbuf = new StringWriter();
		PrintWriter epbuf = new PrintWriter(esbuf);
		e.printStackTrace(epbuf);
		buf.append(htmlspecialchars(esbuf.toString()));
		buf.append("</pre>");
	
		buf.append("</div>");

        _bytes = buf.toString().getBytes();
        _length = _bytes.length;
        _mime = MimeType.Error;
    }

    public void write( OutputStream out ) throws IOException
	{
        out.write( _bytes, 0, (int)_length );
    }


	private String htmlspecialchars(String text)
	{
		text = str_replace(text, "<", "&lt;");
		text = str_replace(text, ">", "&gt;");

		return text;
	}

	private String str_replace(String text, String search, String replace)
	{
		int pos = text.indexOf(search);
		while (pos >= 0)
		{
			String start = text.substring(0, pos);
			String end = text.substring(pos + search.length());
			text = start + replace + end;
			pos = text.indexOf(search);
		}

		return text;
	}
}


