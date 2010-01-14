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
 * Usually runs before the first call of a {@linkplain HttpRequest http request}.
 *
 * @author Willi Schoenborn
 */
public final class PreRequestScopeFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(PreRequestScopeFilter.class);
    
    private final RequestScope scope;
    
    @Inject
    public PreRequestScopeFilter(RequestScope scope) {
        this.scope = Preconditions.checkNotNull(scope, "Scope");
    }
    
    @Override
    public void filter(Call request, Response response, FilterChain chain) throws FilterException {
        scope.enter();
        log.debug("Entered request scope");
        // TODO fix call/request/httprequest confusion
        scope.seed(Call.class, request);
        scope.seed(Response.class, response);
        
        try {
            chain.filter(request, response);
        } catch (FilterException e) {
            scope.exit();
            log.warn("Forced to exit request scope");
            throw e;
        }
    }

}
