/**
 * palava - a java-php-bridge
 * Copyright (C) 2007  CosmoCode GmbH
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

package de.cosmocode.palava.core.command.alias;

import com.google.common.base.Preconditions;

/**
 * Static factory/utility class for {@link Alias}es.
 *
 * @author Willi Schoenborn
 */
public final class Aliases {

    private Aliases() {
        
    }
    
    /**
     * Construct a new {@link Alias}.
     * 
     * @param name the name of the alias 
     * @param packageName the package name
     * @return a new {@link Alias}
     * @throws NullPointerException if name or packageName is null
     */
    public static Alias of(String name, String packageName) {
        return new DefaultAlias(name, packageName);
    }
    
    /**
     * Simple default implementation of the {@link Alias} interface.
     *
     * @author Willi Schoenborn
     */
    private static class DefaultAlias implements Alias {

        private final String name;
        
        private final String packageName;
        
        public DefaultAlias(String name, String packageName) {
            this.name = Preconditions.checkNotNull(name, "Name");
            this.packageName = Preconditions.checkNotNull(packageName, "PackageName");
        }

        @Override
        public String apply(String from) {
            return from.startsWith(name) ? name.replace(name, packageName) : from;
        }
        
    }

}
