package de.project.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.cosmocode.palava.core.request.HttpRequest;
import de.cosmocode.palava.core.request.HttpRequestFilter;

final class LogRequestFilter implements HttpRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(LogRequestFilter.class);

    @Override
    public void after(HttpRequest request) {
        log.debug("HttpRequest on {}, from {} ({})", new Object[] {
            request.getRequestUri(), request.getRemoteAddress(), request.getUserAgent()
        });
    }

    @Override
    public void before(HttpRequest request) {
        // TODO Auto-generated method stub

    }

}
