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

package de.cosmocode.palava.core.request;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.regex.Matcher;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.internal.Maps;

import de.cosmocode.commons.Patterns;
import de.cosmocode.palava.core.session.HttpSession;

/**
 * Default implementation of the {@link HttpRequest}.
 *
 * @author Willi Schoenborn
 */
final class DefaultHttpRequest implements HttpRequest {
    
    private static final String REQUEST_URI = "REQUEST_URI";
    private static final String REMOTE_ADDR = "REMOTE_ADDR";
    private static final String HTTP_USER_AGENT = "HTTP_USER_AGENT";

    private final HttpSession httpSession;
    
    private final Map<Object, Object> context = Maps.newHashMap();
    
    // TODO fill
    private final Map<String, String> serverSuperGlobal = Maps.newHashMap();
    
    @Inject
    public DefaultHttpRequest(HttpSession httpSession) {
        this.httpSession = Preconditions.checkNotNull(httpSession, "HttpSession");
    }

    @Override
    public URI getRequestUri() {
        final String uri = serverSuperGlobal.get(REQUEST_URI);
        Preconditions.checkState(uri != null, "%s not found", REQUEST_URI);
        try {
            return new URI(uri);
        } catch (URISyntaxException e) {
            throw new IllegalStateException(e);
        }
    }
    
    @Override
    public InetAddress getRemoteAddress() {
        final String address = serverSuperGlobal.get(REMOTE_ADDR);
        Preconditions.checkState(address != null, "%s not found", REMOTE_ADDR);
        final byte[] bytes = new byte[4];
        final Matcher matcher = Patterns.INTERNET_ADDRESS.matcher(address);
        Preconditions.checkState(matcher.matches(), "%s is no valid internet address", address);
        for (int i = 1; i <= 4; i++) {
            bytes[i] = Byte.parseByte(matcher.group(i));
        }
        try {
            return Inet4Address.getByAddress(bytes);
        } catch (UnknownHostException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public String getUserAgent() {
        final String agent = serverSuperGlobal.get(HTTP_USER_AGENT);
        Preconditions.checkState(agent != null, "%s not found", HTTP_USER_AGENT);
        return agent;
    }

    @Override
    public HttpSession getHttpSession() {
        return httpSession;
    }

    @Override
    public <K, V> void set(K key, V value) {
        context.put(key, value);
    }

    @Override
    public <K> boolean contains(K key) {
        return context.containsKey(key);
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public <K, V> V get(K key) {
        return (V) context.get(key);
    }

    @Override
    public void destroy() {
        context.clear();
        // TODO all?
        serverSuperGlobal.clear();
    }

}
