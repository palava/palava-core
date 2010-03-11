/**
 * palava - a java-php-bridge
 * Copyright (C) 2007-2010  CosmoCode GmbH
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package de.cosmocode.palava.core.scope;

import java.util.Set;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.inject.Key;
import com.google.inject.OutOfScopeException;
import com.google.inject.Provider;
import com.google.inject.Scope;
import com.google.inject.internal.Sets;

/**
 * Abstract skeleton implementation of the {@link Scope} interface.
 *
 * @author Willi Schoenborn
 * @param <S> the generic scope context type
 */
public abstract class AbstractScope<S extends ScopeContext> implements Scope, Provider<S> {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractScope.class);
    
    /**
     * Checks whether this scope is currently in progress.
     * 
     * @return true if this scope is currently in progress, false otherwise
     */
    protected abstract boolean inProgress();
    
    /**
     * Being called by {@link AbstractScope#enter()} to allow
     * doing work.
     */
    protected void doEnter() {
        
    }
    
    /**
     * Enters this scope.
     */
    public final void enter() {
        Preconditions.checkState(!inProgress(), "%s is already in progress", getClass().getSimpleName());
        LOG.trace("Entering {}", getClass().getSimpleName());
        doEnter();
    }
    
    @Override
    public final <T> Provider<T> scope(final Key<T> key, final Provider<T> provider) {
        return new Provider<T>() {

            @Override
            public T get() {
                LOG.trace("Intercepting scoped request with {} to {}", key, provider);
                if (!inProgress()) {
                    throw new OutOfScopeException(String.format("Can't access %s outside of a %s block", 
                        key, AbstractScope.this
                    ));
                }
                final ScopeContext context = AbstractScope.this.get();
                final T cached = context.<Key<T>, T>get(key);
                // is there a cached version?
                if (cached == null && !context.contains(key)) {
                    final T unscoped = provider.get();
                    context.set(key, unscoped);
                    LOG.trace("No cached version for {} found, created {}", key, unscoped);
                    return unscoped;
                } else {
                    LOG.trace("Found cached version for {}: {}", key, cached);
                    return cached;
                }
            }
            
            @Override
            public String toString() {
                return String.format("%s[%s]", provider, AbstractScope.this);
            }

        };
    }
    
    /**
     * Being called by {@link AbstractScope#exit()} to allow
     * doing work.
     */
    protected void doExit() {
        
    }
    
    /**
     * Exits this scope.
     */
    public final void exit() {
        if (!inProgress()) {
            LOG.warn("{} already exited", getClass().getSimpleName());
            return;
        }
        LOG.trace("Exiting {}", getClass().getSimpleName());
        
        final ScopeContext context = get();
        final Set<Object> keys = Sets.newHashSet();
        for (Entry<Object, Object> entry : context) {
            keys.add(entry.getKey());
            if (entry.getKey() instanceof Destroyable) {
                try {
                    LOG.trace("Destroying key {}", entry.getKey());
                    Destroyable.class.cast(entry.getKey()).destroy();
                /*CHECKSTYLE:OFF*/
                } catch (RuntimeException e) {
                /*CHECKSTYLE:ON*/
                    LOG.error("Failed to destroy scoped key: {}", e);
                }
            }
            if (entry.getValue() instanceof Destroyable) {
                try {
                    LOG.trace("Destroying value {}", entry.getValue());
                    Destroyable.class.cast(entry.getValue()).destroy();
                /*CHECKSTYLE:OFF*/
                } catch (RuntimeException e) {
                /*CHECKSTYLE:ON*/
                    LOG.error("Failed to destroy scoped value: {}", e);
                }
            }
        }
        
        for (Object key : keys) {
            context.remove(key);
        }
        
        doExit();
    }
    
    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

}
