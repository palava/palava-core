package de.cosmocode.palava.core.request;

import java.util.Map;

import de.cosmocode.palava.core.session.HttpSession;

final class DefaultHttpRequestFactory implements HttpRequestFactory {

    @Override
    public HttpRequest create(HttpSession session, Map<String, String> serverVariable) {
        return new DefaultHttpRequest(session, serverVariable);
    }

}
