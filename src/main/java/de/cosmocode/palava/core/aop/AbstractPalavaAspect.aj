package de.cosmocode.palava.core.aop;

import org.aspectj.lang.annotation.SuppressAjWarnings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.inject.Guice;
import com.google.inject.Injector;

public abstract aspect AbstractPalavaAspect {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractPalavaAspect.class);
    
    private boolean alreadyInjected = false;
    
    pointcut createInjector(): call(Injector Guice.createInjector(..));
    
    @SuppressAjWarnings("adviceDidNotMatch")
    after() returning (Injector injector): createInjector() {
        LOG.trace("Injecting members on {}", this);
        Preconditions.checkState(!alreadyInjected, "Members have been already injected on {}", this);
        injector.injectMembers(this);
    }

}
