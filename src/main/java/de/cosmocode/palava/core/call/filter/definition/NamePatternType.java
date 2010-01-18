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

package de.cosmocode.palava.core.call.filter.definition;

/**
 * An enum style factory which creates concrete {@link NamePatternMatcher}
 * instances based on their specific semantics.
 *
 * @author Willi Schoenborn
 */
public enum NamePatternType {

    /**
     * Defines simple servlet style name pattern matching
     * using *s.
     */
    SIMPLE {
        
        @Override
        public NamePatternMatcher matcher(String pattern) {
            return new SimpleNamePatternMatcher(pattern);
        }
        
    },
    
    /**
     * Defines regex style name pattern matching.
     */
    REGEX {
        
        @Override
        public NamePatternMatcher matcher(String pattern) {
            return new RegexNamePatternMatcher(pattern);
        }
        
    };
    
    /**
     * Creates a new {@link NamePatternMatcher} using the semantics
     * of this {@link NamePatternType}.
     * 
     * @param pattern the name pattern
     * @return a new {@link NamePatternMatcher}
     */
    public abstract NamePatternMatcher matcher(String pattern);
    
}
