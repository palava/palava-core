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

package de.cosmocode.palava.bridge.call.filter.definition;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;

import de.cosmocode.palava.bridge.command.Command;
import de.cosmocode.palava.bridge.command.Commands;

/**
 * A {@link CommandMatcher} which uses a servlet style like
 * matching algorithm.
 *
 * @author Willi Schoenborn
 */
final class SimpleCommandNameMatcher implements Predicate<Command> {

    /**
     * A {@link PatternType} describes the kind of a simple name pattern.
     *
     * @author Willi Schoenborn
     */
    private static enum PatternType {
        
        PREFIX {
            
            @Override
            public boolean matches(String pattern, String name) {
                return name.endsWith(pattern);
            }
            
        },
        
        SUFFIX {
            
            @Override
            public boolean matches(String pattern, String name) {
                return name.startsWith(pattern);
            }
            
        },
        
        LITERAL {
          
            @Override
            public boolean matches(String pattern, String name) {
                return pattern.equals(name);
            }
            
        };
        
        public abstract boolean matches(String pattern, String name);
        
    }

    private final String pattern;
    
    private final PatternType patternType;
    
    public SimpleCommandNameMatcher(String pattern) {
        Preconditions.checkNotNull(pattern, "Pattern");
        
        if (pattern.startsWith("*")) {
            this.pattern = pattern.substring(1);
            this.patternType = PatternType.PREFIX;
        } else if (pattern.endsWith("*")) {
            this.pattern = pattern.substring(0, pattern.length() - 1);
            this.patternType = PatternType.SUFFIX;
        } else {
            this.pattern = pattern;
            this.patternType = PatternType.LITERAL;
        }
    }
    
    @Override
    public boolean apply(Command command) {
        return patternType.matches(pattern, Commands.getClass(command).getName());
    }

}
