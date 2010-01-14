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

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.google.common.base.Preconditions;

/**
 * sends a content object to the palava frontend.
 * 
 * @author Tobias Sarnowski
 * @author Willi Schoenborn
 */
// TODO package private
public final class DefaultResponse implements Response {

    private final OutputStream out;

    private Content content;

    private boolean sent;

    public DefaultResponse(OutputStream out) {
        this.out = Preconditions.checkNotNull(out);
    }

    @Override
    public void setContent(Content content) {
        this.content = content;
    }
    
    @Override
    public Content getContent() {
        return content;
    }

    @Override
    public boolean hasContent() {
        return content != null;
    }
    
    @Override
    public void send() throws IOException {
        Preconditions.checkNotNull(content, "Content");
        Preconditions.checkState(!sent, "Already sent");
        final BufferedOutputStream buffered = new BufferedOutputStream(out);

        // header
        final String header = String.format("%s://(%s)?", content.getMimeType(), content.getLength());
        buffered.write(header.getBytes());

        // body
        content.write(buffered);
        buffered.flush();

        sent = true;
    }

    @Override
    public boolean sent() {
        return sent;
    }

}
