package de.cosmocode.palava.core.scope;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;


/**
 * A {@link ThreadLocal} based {@link UnitOfWorkScope} implementation.
 *
 * @author Willi Schoenborn
 */
public final class ThreadUnitOfWorkScope extends AbstractScope<ScopeContext> implements UnitOfWorkScope {

    private static final Logger LOG = LoggerFactory.getLogger(ThreadUnitOfWorkScope.class);
    
    private final ThreadLocal<ScopeContext> context = new ThreadLocal<ScopeContext>();
    
    @Override
    public void enter() {
        Preconditions.checkState(context.get() == null, "Scope already entered");
        LOG.trace("Entering {}", this);
        context.set(new SimpleScopeContext());
    }

    @Override
    public void exit() {
        Preconditions.checkState(context.get() != null, "No scope block in progress");
        LOG.trace("Exiting {}", this);
        context.remove();
    }

    @Override
    public ScopeContext get() {
        return context.get();
    }

}
