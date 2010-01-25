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

import java.lang.annotation.Annotation;
import java.util.List;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import de.cosmocode.palava.core.command.Command;
import de.cosmocode.palava.core.command.Commands;

/**
 * Static factory class for {@link CommandMatcher}s.
 *
 * @author Willi Schoenborn
 */
public final class Matchers {

    private static final Predicate<Command> ANY = new Predicate<Command>() {
        
        @Override
        public boolean apply(Command type) {
            return true;
        }
        
    };
    
    private Matchers() {
        
    }
    
    /**
     * Provides a {@link Predicate<Command>} which matches every command.
     * 
     * @return a {@link Predicate<Command>} which always return true
     */
    public static Predicate<Command> any() {
        return ANY;
    }
    
    /**
     * Provides a {@link Predicate<Command>} which uses the given predicate to decide
     * whether a given {@link Command} matches.
     * 
     * @param predicate the backing predicate
     * @return a {@link Predicate<Command>} backed by a {@link Predicate}
     */
    public static Predicate<Command> ofPredicate(final Predicate<Command> predicate) {
        return new Predicate<Command>() {
            
            @Override
            public boolean apply(Command input) {
                return predicate.apply(input);
            }
            
        };
    }
    
    /**
     * 
     * 
     * @param pattern
     * @param patterns
     * @return
     */
    public static Predicate<Command> named(String pattern, String... patterns) {
        final ImmutableList.Builder<String> builder = ImmutableList.builder();
        final ImmutableList<String> list = builder.add(pattern).add(patterns).build();
        final List<Predicate<Command>> matchers = Lists.transform(list, new Function<String, Predicate<Command>>() {
           
            @Override
            public Predicate<Command> apply(String from) {
                return NamePatternType.SIMPLE.matcher(from);
            }
            
        });
        return Matchers.ofPredicate(Predicates.or(matchers));
    }
    
    /**
     * 
     * @param pattern
     * @param patterns
     * @return
     */
    public static Predicate<Command> regex(String pattern, String... patterns) {
        final ImmutableList.Builder<String> builder = ImmutableList.builder();
        final ImmutableList<String> list = builder.add(pattern).add(patterns).build();
        final List<Predicate<Command>> matchers = Lists.transform(list, new Function<String, Predicate<Command>>() {
           
            @Override
            public Predicate<Command> apply(String from) {
                return NamePatternType.REGEX.matcher(from);
            }
            
        });
        return Matchers.ofPredicate(Predicates.or(matchers));
    }
    
    /**
     * 
     * @param annotation
     * @return
     */
    public static Predicate<Command> annotatedWith(final Class<? extends Annotation> annotation) {
        return new Predicate<Command>() {
            
            @Override
            public boolean apply(Command command) {
                return Commands.getClass(command).isAnnotationPresent(annotation);
            }
            
        };
    }
    
    /**
     * 
     * @param superClass
     * @return
     */
    public static Predicate<Command> subClassesOf(final Class<?> superClass) {
        return new Predicate<Command>() {
            
            @Override
            public boolean apply(Command command) {
                return superClass.isAssignableFrom(Commands.getClass(command));
            }
            
        };
    }
    
}
