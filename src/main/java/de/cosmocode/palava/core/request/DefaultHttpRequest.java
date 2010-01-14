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
