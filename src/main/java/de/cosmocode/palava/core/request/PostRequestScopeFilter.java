package de.cosmocode.palava.core.request;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;

import de.cosmocode.palava.core.command.filter.Filter;
import de.cosmocode.palava.core.command.filter.FilterChain;
import de.cosmocode.palava.core.command.filter.FilterException;
import de.cosmocode.palava.core.protocol.Call;
import de.cosmocode.palava.core.protocol.Response;

/**
 * Usually runs after the last call of a {@linkplain HttpRequest http request}.
 *
 * @author Willi Schoenborn
 */
public final class PostRequestScopeFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(PostRequestScopeFilter.class);
    
    private final RequestScope scope;
    
    @Inject
    public PostRequestScopeFilter(RequestScope scope) {
        this.scope = Preconditions.checkNotNull(scope, "Scope");
    }

    @Override
    public void filter(Call call, Response response, FilterChain chain) throws FilterException {
        try {
            chain.filter(call, response);
        } finally {
            scope.exit();
            log.debug("Exited request scope");
        }
    }

}
