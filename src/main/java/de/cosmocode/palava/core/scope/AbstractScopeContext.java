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

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;


/**
 * Abstract skeleton implementation of the {@link ScopeContext} interface.
 *
 * @author Willi Schoenborn
 */
public abstract class AbstractScopeContext implements ScopeContext {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractScopeContext.class);
    
    /**
     * Provide the underlying context map. Allows sub classes to 
     * plug-in different map implementations.
     * 
     * @return a context map
     */
    protected abstract Map<Object, Object> context();

    @Override
    public <K, V> void set(K key, V value) {
        Preconditions.checkNotNull(key, "Key");
        context().put(key, value);
    };
    
    @Override
    @SuppressWarnings("unchecked")
    public <K, V> V get(K key) {
        Preconditions.checkNotNull(key, "Key");
        return (V) context().get(key);
    };

    @Override
    public <K> boolean contains(K key) {
        Preconditions.checkNotNull(key, "Key");
        return context().containsKey(key);
    };
    
    @Override
    @SuppressWarnings("unchecked")
    public <K, V> V remove(K key) {
        Preconditions.checkNotNull(key, "Key");
        return (V) context().remove(key);
    };
    
    @Override
    public <K, V> void putAll(Map<? extends K, ? extends V> map) {
        Preconditions.checkNotNull(map, "Map");
        context().putAll(map);
    }
    
    @Override
    public Iterator<Entry<Object, Object>> iterator() {
        return context().entrySet().iterator();
    }
    
    @Override
    public void clear() {
        final Iterator<Entry<Object, Object>> iterator = iterator();
        
        while (iterator.hasNext()) {
            final Entry<Object, Object> entry = iterator.next();
            
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
            
            iterator.remove();
        }
    }
    
}
