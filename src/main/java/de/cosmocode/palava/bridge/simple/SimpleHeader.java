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

import com.google.common.base.Preconditions;

/**
 * Default implementation of the the {@link Header} interface.
 *
 * @author Willi Schoenborn
 */
final class SimpleHeader implements Header {

    private final CallType requestType;
    private final String aliasedName;
    private final String sessionId;
    private final int contentLength;
    
    public SimpleHeader(CallType requestType, String aliasedName, String sessionId, int contentLength) {
        this.requestType = Preconditions.checkNotNull(requestType, "RequestType");
        this.aliasedName = Preconditions.checkNotNull(aliasedName, "AliasedName");
        this.sessionId = Preconditions.checkNotNull(sessionId, "SessionId");
        this.contentLength = contentLength;
    }

    @Override
    public CallType getCallType() {
        return requestType;
    }

    @Override
    public String getAliasedName() {
        return aliasedName;
    }

    @Override
    public String getSessionId() {
        return sessionId;
    }

    @Override
    public int getContentLength() {
        return contentLength;
    }

}
