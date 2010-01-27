/**
 * palava - a java-php-bridge
 * Copyright (C) 2007-2010  CosmoCode GmbH
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

package de.cosmocode.palava.core.bridge.simple.content;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;

import com.google.common.base.Preconditions;


/**
 * An error occured, this ships it to the browser.
 * 
 * @author Tobias Sarnowski
 * @author Willi Schoenborn
 */
public class ErrorContent extends AbstractContent {
    
    private final byte [] bytes;

    private ErrorContent(Exception e) {
        super(MimeType.ERROR);
        
        // FIXME php is good enough to create the html itself, it's ugly here
        final StringBuilder builder = new StringBuilder();
        builder.append("<div style='background-color: #ff0000 !important; margin: 20px; font-family: Lucida Grande, " +
            "Verdana, Sans-serif; font-size: 12px; color: #ffffff !important; border-color: #000000 !important; " +
            "border-width: 2px; border-style: solid; padding: 10px; text-align: left'>");
        builder.append("<span style='font-weight: bold; font-size: 16px; background-color: #ff0000 !important'>" +
            "Palava Error:  ");
        builder.append(htmlspecialchars(e.toString()));
        builder.append("</span>");
        builder.append("<pre style='background-color: #ff0000 !important'>");
        final StringWriter writer = new StringWriter();
        e.printStackTrace(new PrintWriter(writer));
        builder.append(htmlspecialchars(writer.toString()));
        builder.append("</pre>");
        builder.append("</div>");

        bytes = builder.toString().getBytes();
    }

    private String htmlspecialchars(String text) {
        return text.replace("<", "&lt;").replace(">", "&gt;");
    }
    
    @Override
    public long getLength() {
        return bytes.length;
    }
    
    @Override
    public void write(OutputStream out) throws IOException {
        out.write(bytes, 0, bytes.length);
    }
    
    public static ErrorContent create(Exception exception) {
        Preconditions.checkNotNull(exception, "Exception");
        return new ErrorContent(exception);
    }

}


