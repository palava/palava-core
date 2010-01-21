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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.cosmocode.palava.core.call.Call;
import de.cosmocode.palava.core.command.Command;
import de.cosmocode.palava.core.request.HttpRequest;

final class OpenCall implements Call {

    private static final Logger log = LoggerFactory.getLogger(OpenCall.class);

    @Override
    public HttpRequest getHttpRequest() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Command getCommand() {
        throw new UnsupportedOperationException();
    }

    @Override
    public InputStream getInputStream() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Header getHeader() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void discard() throws ConnectionLostException, IOException {
        // TODO Auto-generated method stub

    }

}
