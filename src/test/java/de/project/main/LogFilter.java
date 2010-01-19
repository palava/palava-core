package de.project.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.cosmocode.palava.core.call.Call;
import de.cosmocode.palava.core.call.filter.Filter;
import de.cosmocode.palava.core.call.filter.FilterChain;
import de.cosmocode.palava.core.call.filter.FilterException;
import de.cosmocode.palava.core.protocol.content.Content;

final class LogFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(LogFilter.class);

    @Override
    public Content filter(Call call, FilterChain chain) throws FilterException {
        log.debug("LOGGED {}", call);
        return chain.filter(call);
    }

}
