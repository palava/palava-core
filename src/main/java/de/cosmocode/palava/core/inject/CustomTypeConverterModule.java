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

import com.google.common.base.Preconditions;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matcher;
import com.google.inject.matcher.Matchers;
import com.google.inject.spi.TypeConverter;

/**
 * Abstract module which allows easy configuration of {@link TypeConverter}s.
 *
 * @author Willi Schoenborn
 */
public abstract class CustomTypeConverterModule extends AbstractModule {

    /**
     * Registers the given converter for the specified type.
     * 
     * @param type the value type
     * @param converter the converter for type
     * @throws NullPointerException if type or converter is null
     */
    protected final void register(Class<?> type, TypeConverter converter) {
        Preconditions.checkNotNull(type, "Type");
        Preconditions.checkNotNull(converter, "Converter");
        register(TypeLiteral.get(type), converter);
    }

    /**
     * Registers the given converter for the specified literal.
     * 
     * @param literal the value type literal
     * @param converter the converter for type
     * @throws NullPointerException if literal or converter is null
     */
    protected final void register(TypeLiteral<?> literal, TypeConverter converter) {
        Preconditions.checkNotNull(literal, "literal");
        Preconditions.checkNotNull(converter, "Converter");
        register(Matchers.only(literal), converter);
    }

    /**
     * Registers the given converter for the specified matcher.
     * 
     * @param matcher the value type matcher
     * @param converter the converter for type
     * @throws NullPointerException if matcher or converter is null
     */
    protected final void register(Matcher<? super TypeLiteral<?>> matcher, TypeConverter converter) {
        Preconditions.checkNotNull(matcher, "Matcher");
        Preconditions.checkNotNull(converter, "Converter");
        binder().convertToTypes(matcher, converter);
    }

}
