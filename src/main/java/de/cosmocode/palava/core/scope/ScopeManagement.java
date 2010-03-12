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

import com.google.inject.internal.Sets;

/**
 * Static utility class for scope related management tasks.
 *
 * @author Willi Schoenborn
 */
public final class ScopeManagement {

    private static final Logger LOG = LoggerFactory.getLogger(ScopeManagement.class);

    private ScopeManagement() {
        
    }
    
    /**
     * Destroys the given context by calling {@link Destroyable#destroy()}
     * on all keys and values are {@link Destroyable} and then removes all
     * entries.
     * 
     * @param context the context being destroyed
     */
    public static void destroy(ScopeContext context) {
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
    }
    
}
