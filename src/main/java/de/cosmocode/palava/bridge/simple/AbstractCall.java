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

package de.cosmocode.palava.bridge.simple;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

import de.cosmocode.commons.io.UnclosableInputStream;
import de.cosmocode.palava.bridge.call.Arguments;
import de.cosmocode.palava.bridge.call.Call;
import de.cosmocode.palava.bridge.command.Command;
import de.cosmocode.palava.bridge.request.HttpRequest;

/**
 * Abstract implementation of the {@link Call} interface.
 * 
 * @author Tobias Sarnowski
 * @author Willi Schoenborn
 */
abstract class AbstractCall implements Call {
    
    protected static final Charset CHARSET = Charset.forName("UTF-8");
    
    private final Logger log = LoggerFactory.getLogger(AbstractCall.class);

    private final HttpRequest request;

    private final Command command;
    
    private final Header header;
    
    private final InputStream stream;
    
    private int totalBytesRead;

    AbstractCall(HttpRequest request, Command command, Header header, InputStream stream) {
        // TODO we need nullchecks here!
//        this.request = Preconditions.checkNotNull(request, "Request");
//        this.command = Preconditions.checkNotNull(command, "Command");
        this.request = request;
        this.command = command;
        this.stream = new UnclosableInputStream(Preconditions.checkNotNull(stream, "Stream"));
        this.header = Preconditions.checkNotNull(header, "Header");
    }
    
    @Override
    public Arguments getArguments() {
        throw new UnsupportedOperationException(getClass().getSimpleName() + " does not support arguments");
    }
    
    @Override
    public HttpRequest getHttpRequest() {
        return request;
    }
    
    @Override
    public Command getCommand() {
        return command;
    }

    @Override
    public InputStream getInputStream() {
        return stream;
    }
    
    @Override
    public Header getHeader() {
        return header;
    }

    /**
     * 
     * @param data
     * @return
     * @throws ConnectionLostException
     */
    protected final int read(byte[] data) throws ConnectionLostException {
        final long max = header.getContentLength();

        if (totalBytesRead >= max) {
            throw new ConnectionLostException("not allowed to read enough bytes, content-length reached");
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

}
