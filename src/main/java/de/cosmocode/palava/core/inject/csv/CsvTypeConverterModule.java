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

package de.cosmocode.palava.core.inject.csv;

import java.util.Collection;
import java.util.List;

import com.google.inject.TypeLiteral;
import com.google.inject.spi.TypeConverter;

import de.cosmocode.palava.core.inject.CustomTypeConverterModule;

/**
 * Custom typeconverter module which registers {@link CsvConverter} and {@link CsvIteratorConverter}.
 *
 * @since 2.9
 * @author Willi Schoenborn
 */
public final class CsvTypeConverterModule extends CustomTypeConverterModule {

    @Override
    protected void configure() {
        
        final TypeConverter csvConverter = new CsvConverter();
        register(new TypeLiteral<Iterable<String[]>>() { }, csvConverter);
        register(new TypeLiteral<Collection<String[]>>() { }, csvConverter);
        register(new TypeLiteral<List<String[]>>() { }, csvConverter);
        
        register(CsvIteratorConverter.LITERAL, new CsvIteratorConverter());
    }

}
