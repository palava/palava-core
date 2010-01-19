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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

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
    
    private final Logger log = LoggerFactory.getLogger(AbstractCall.class);
    
    protected static final Charset CHARSET = Charset.forName("UTF-8");

    private final Header header;
    private final InputStream stream;
    private int totalBytesRead;

    public AbstractCall(Header header, InputStream stream) {
        Preconditions.checkNotNull(header, "Header");
        Preconditions.checkNotNull(stream, "Stream");
        this.stream = new UncloseableInputStream(stream);
        this.header = header;
    }

    @Override
    public InputStream getInputStream() {
        return stream;
    }
    
    @Override
    public Header getHeader() {
        return header;
    }

    protected final int read(byte[] data) throws ConnectionLostException, IOException {
        final long max = header.getContentLength();
        log.debug("Max bytes available: {}", max);
        log.debug("Already read: {}", totalBytesRead);
        log.debug("Attempting to read {} bytes", data.length);
        
        if (totalBytesRead >= max) {
            throw new IOException("not allowed to read enough bytes, content-length reached");
        }

        int read;
        
        try {
            read = stream.read(data, 0, data.length);
        } catch (IOException ioe) {
            throw new ConnectionLostException();
        }
        
        if (read == -1) {
            throw new ConnectionLostException();
        }

        totalBytesRead += read;
        return read;
    }

    @Override
    public final void discard() throws ConnectionLostException, IOException {
        if (totalBytesRead < header.getContentLength()) {
            read(new byte[header.getContentLength() - totalBytesRead]);
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
