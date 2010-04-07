package de.cosmocode.palava.core.scope;

import org.aspectj.lang.annotation.SuppressAjWarnings;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;

import de.cosmocode.palava.core.aop.AbstractPalavaAspect;

public final aspect UnitOfWorkScopeAspect extends AbstractPalavaAspect issingleton() {

    private UnitOfWorkScope scope;
    
    @Inject
    void setUnitOfWorkScope(UnitOfWorkScope scope) {
        this.scope = Preconditions.checkNotNull(scope, "Scope");
    }
    
    pointcut unitOfWork(): execution(@UnitOfWork * *.*(..));
    
    @SuppressAjWarnings("adviceDidNotMatch")
    Object around(): unitOfWork() {
        scope.enter();
        try {
            return proceed();
        } finally {
            scope.exit();
        }
    }
    
}
