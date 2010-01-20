package de.project.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.cosmocode.palava.core.call.Call;
import de.cosmocode.palava.core.call.filter.Filter;
import de.cosmocode.palava.core.call.filter.FilterChain;
import de.cosmocode.palava.core.call.filter.FilterException;
import de.cosmocode.palava.core.command.Commands;
import de.cosmocode.palava.core.protocol.content.Content;

/**
 * 
 *
 * @author Willi Schoenborn
 */
final class LogFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(LogFilter.class);

    @Override
    public Content filter(Call call, FilterChain chain) throws FilterException {
        log.debug("Running command: {}", Commands.getClass(call.getCommand()));
        try {
            return chain.filter(call);
        } finally {
            log.debug("Finished command: {}", Commands.getClass(call.getCommand()));
        }
    }

}
