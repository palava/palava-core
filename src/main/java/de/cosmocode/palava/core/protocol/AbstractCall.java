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
import java.nio.charset.Charset;

import com.google.common.base.Preconditions;

import de.cosmocode.palava.ConnectionLostException;
import de.cosmocode.palava.RequestHeader;
import de.cosmocode.palava.UncloseableInputStream;
import de.cosmocode.palava.core.call.Call;
import de.cosmocode.palava.core.command.Command;
import de.cosmocode.palava.core.request.HttpRequest;

/**
 * Abstract implementation of the {@link Call} interface.
 * 
 * @author Tobias Sarnowski
 * @author Willi Schoenborn
 */
abstract class AbstractCall implements Call {
    
    protected static final Charset CHARSET = Charset.forName("UTF-8");

    private final RequestHeader header;
    private final InputStream stream;
    private long read;

    public AbstractCall(RequestHeader header, InputStream stream) {
        Preconditions.checkNotNull(header, "Header");
        Preconditions.checkNotNull(stream, "Stream");
        this.stream = new UncloseableInputStream(stream);
        this.header = header;
        this.read = header.getContentLength();
    }

    @Override
    public InputStream getInputStream() {
        return stream;
    }
    
    @Override
    public RequestHeader getHeader() {
        return header;
    }

    @Override
    public int read(byte[] data) throws ConnectionLostException, IOException {
        final long max = header.getContentLength();
        if (max - read - data.length < 0) {
            throw new IOException("not allowed to read enough bytes, content-length reached");
        }

        int written;
        
        try {
            written = stream.read(data, 0, data.length);
        } catch (IOException ioe) {
            throw new ConnectionLostException();
        }
        
        if (written == -1) {
            throw new ConnectionLostException();
        }

        read = read + written;
        return written;
    }

    @Override
    public void freeInputStream() throws ConnectionLostException, IOException {
        final byte[] buffer = new byte[1];
        while (read < header.getContentLength()) {
            read(buffer);
        }
    }
    
    @Override
    public HttpRequest getHttpRequest() {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public Command getCommand() {
        // TODO Auto-generated method stub
        return null;
    }

}
