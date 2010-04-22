/**
 * Copyright 2010 CosmoCode GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.cosmocode.palava.core.inject;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

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
public final class TypeConverterModule implements Module {

    private static final Logger LOG = LoggerFactory.getLogger(TypeConverterModule.class);
    
    private static final String CLASSPATH_PREFIX = "classpath:";

    /**
     * Reusable {@link TypeConverter} for {@link URL}s.
     *
     * @author Willi Schoenborn
     */
    private static final class UrlConverter implements TypeConverter {
        
        @Override
        public URL convert(String value, TypeLiteral<?> literal) {
            try {
                if (value.startsWith(CLASSPATH_PREFIX)) {
                    LOG.trace("Considering {} to be a classpath resource", value);
                    return getClass().getClassLoader().getResource(value.substring(CLASSPATH_PREFIX.length()));
                } else {
                    return new URL(value);
                }
            } catch (MalformedURLException e) {
                throw new IllegalArgumentException(e);
            }
        }
        
    };
    
    private static final UrlConverter URL_CONVERTER = new UrlConverter();
    
    private static final TypeConverter FILE_CONVERTER = new TypeConverter() {
        
        @Override
        public Object convert(String value, TypeLiteral<?> literal) {
            final URL url = URL_CONVERTER.convert(value, literal);
            Preconditions.checkNotNull(url, "%s not found", value);
            final String protocol = url.getProtocol();
            Preconditions.checkArgument("file".equals(protocol), "protocol must be file but was %s", protocol);
            return new File(url.getFile());
        }
        
    };
    
    private static final TypeConverter PROPERTIES_CONVERTER = new TypeConverter() {
        
        @Override
        public Object convert(String value, TypeLiteral<?> literal) {
            final Properties properties = new Properties();
            try {
                final URL url = URL_CONVERTER.convert(value, literal);
                Preconditions.checkNotNull(url, "%s not found", value);
                properties.load(url.openStream());
            } catch (IOException e) {
                throw new IllegalArgumentException(e);
            }
            return properties;
        }
        
    };
    
    private static final TypeConverter LOGGER_CONVERTER = new TypeConverter() {
        
        @Override
        public Object convert(String value, TypeLiteral<?> literal) {
            return LoggerFactory.getLogger(value);
        }
    };
    
    private static Matcher<TypeLiteral<?>> equalsTo(final Class<?> type) {
        Preconditions.checkNotNull(type, "Type");
        return new AbstractMatcher<TypeLiteral<?>>() {
            
            @Override
            public boolean matches(TypeLiteral<?> literal) {
                return literal.getRawType().equals(type);
            }
            
        };
    }

    @Override
    public void configure(Binder binder) {
        LOG.debug("Registering url type converter");
        binder.convertToTypes(equalsTo(URL.class), URL_CONVERTER);
        LOG.debug("Registering file type converter");
        binder.convertToTypes(equalsTo(File.class), FILE_CONVERTER);
        LOG.debug("Registering properties type converter");
        binder.convertToTypes(equalsTo(Properties.class), PROPERTIES_CONVERTER);
        LOG.debug("Registering logger converter");
        binder.convertToTypes(equalsTo(Logger.class), LOGGER_CONVERTER);
    }

}
