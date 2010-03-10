package de.cosmocode.palava.core.scope;

import java.util.Map;

import com.google.common.base.Preconditions;
import com.google.inject.internal.Maps;

/**
 * Simple implementation of the {@link ScopeContext} interface.
 *
 * @author Willi Schoenborn
 */
public final class SimpleScopeContext extends AbstractScopeContext {

    private final Map<Object, Object> context;

    public SimpleScopeContext(Map<Object, Object> context) {
        this.context = Preconditions.checkNotNull(context);
    }
    
    public SimpleScopeContext() {
        this(Maps.newHashMap());
    }
    
    @Override
    protected Map<Object, Object> context() {
        return context;
    }
    
}
