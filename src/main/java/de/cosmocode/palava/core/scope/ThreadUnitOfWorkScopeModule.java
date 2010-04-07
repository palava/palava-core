package de.cosmocode.palava.core.scope;

import com.google.inject.Binder;
import com.google.inject.Module;

/**
 * Binds the {@link ThreadUnitOfWorkScope} to {@link UnitOfWork}.
 *
 * @author Willi Schoenborn
 */
public final class ThreadUnitOfWorkScopeModule implements Module {

    @Override
    public void configure(Binder binder) {
        final UnitOfWorkScope scope = new ThreadUnitOfWorkScope();
        binder.requestInjection(scope);
        binder.bindScope(UnitOfWork.class, scope);
        binder.bind(UnitOfWorkScope.class).toInstance(scope);
    }

}
