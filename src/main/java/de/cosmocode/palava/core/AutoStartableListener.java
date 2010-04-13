package de.cosmocode.palava.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.spi.InjectionListener;

import de.cosmocode.palava.core.lifecycle.AutoStartable;

/**
 * {@link InjectionListener} which handles {@link AutoStartable}s.
 *
 * @author Willi Schoenborn
 * @param <I>
 */
public final class AutoStartableListener<I> implements InjectionListener<I> {

    private static final Logger LOG = LoggerFactory.getLogger(AutoStartableListener.class);

    @Override
    public void afterInjection(I injectee) {
        if (injectee instanceof AutoStartable) {
            LOG.info("Autostarting service {}", injectee);
            AutoStartable.class.cast(injectee).start();
        }
    }
    
}
