package de.cosmocode.palava.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.spi.InjectionListener;

import de.cosmocode.palava.core.service.lifecycle.Startable;

/**
 * {@link InjectionListener} which handles {@link Startable}s.
 *
 * @author Willi Schoenborn
 * @param <I>
 */
final class StartableListener<I> implements InjectionListener<I> {

    private static final Logger log = LoggerFactory.getLogger(StartableListener.class);

    @Override
    public void afterInjection(I injectee) {
        if (injectee instanceof Startable) {
            log.info("Starting service {}", injectee);
            Startable.class.cast(injectee).start();
        }
    }

    
    
}
