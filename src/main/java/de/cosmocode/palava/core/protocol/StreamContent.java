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
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;

import de.cosmocode.palava.MimeType;

/**
 * General content to return streams.
 * 
 * @author Tobias Sarnowski
 * @author Willi Schoenborn
 */
public class StreamContent extends AbstractContent {
    
    private final InputStream stream;
    private final int length;
    
    public StreamContent(InputStream stream, long length, MimeType mime) {
        super(mime);
        this.stream = stream;
        this.length = (int) length;
    }
    
    /** 
     * Returns the underlying inputstream.
     * 
     * note that if you read from the input stream manually, 
     * the write StreamContent.write method will fail
     *
     * @return InputStream
     */
    public InputStream getInputStream() {
        return stream;
    }
    
    @Override
    public long getLength() {
        return length;
    }

    @Override
    public void write(OutputStream out) throws IOException {
        // TODO use length
        IOUtils.copy(stream, out);
    }
    
}
