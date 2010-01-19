package de.cosmocode.palava.core.request;

import java.util.Map;

import de.cosmocode.palava.core.session.HttpSession;

public interface HttpRequestFactory {

    HttpRequest create(HttpSession session, Map<String, String> serverVariable);
    
}
