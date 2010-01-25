package de.cosmocode.palava.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.spi.InjectionListener;

import de.cosmocode.palava.core.service.lifecycle.Initializable;

/**
 * {@link InjectionListener} which handles {@link Initializable}s.
 *
 * @author Willi Schoenborn
 * @param <I>
 */
final class InitializableListener<I> implements InjectionListener<I> {

    private static final Logger log = LoggerFactory.getLogger(InitializableListener.class);

    @Override
    public void afterInjection(I injectee) {
        if (injectee instanceof Initializable) {
            log.info("Initializing service {}", injectee);
            Initializable.class.cast(injectee).initialize();
        }
    }
    
}
