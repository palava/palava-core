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

package de.cosmocode.palava.core.inject;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.AbstractMatcher;
import com.google.inject.matcher.Matcher;
import com.google.inject.spi.TypeConverter;

/**
 * A {@link Module} for custom {@link TypeConverter}s and injection
 * related bindings.
 *
 * @author Willi Schoenborn
 */
public final class InjectionModule implements Module {

    private static final Logger log = LoggerFactory.getLogger(InjectionModule.class);
    
    private static final TypeConverter FILE_CONVERTER = new TypeConverter() {
        
        @Override
        public Object convert(String value, TypeLiteral<?> literal) {
            return new File(value);
        }
        
    };
    
    private static final TypeConverter URL_CONVERTER = new TypeConverter() {
        
        @Override
        public Object convert(String value, TypeLiteral<?> literal) {
            try {
                return new URL(value);
            } catch (MalformedURLException e) {
                throw new IllegalArgumentException(e);
            }
        }
    };
    
    private static Matcher<TypeLiteral<?>> subclasseOf(final Class<?> type) {
        Preconditions.checkNotNull(type, "Type");
        return new AbstractMatcher<TypeLiteral<?>>() {
            
            @Override
            public boolean matches(TypeLiteral<?> literal) {
                return type.isAssignableFrom(literal.getRawType());
            }
            
        };
    }

    @Override
    public void configure(Binder binder) {
        log.debug("Registering file type converter");
        binder.convertToTypes(subclasseOf(File.class), FILE_CONVERTER);
        log.debug("Registering url type converter");
        binder.convertToTypes(subclasseOf(URL.class), URL_CONVERTER);
    }

}
