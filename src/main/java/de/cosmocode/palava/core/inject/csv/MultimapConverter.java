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

import java.util.List;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.TypeConverter;


/**
 * A {@link TypeConverter} for {@link ListMultimap}s from .csv files.
 *
 * <p>
 *   This converter relies upon {@link CsvConverter} and maps
 *   the read lines into a multimap using the following semantics:<br />
 *   <ul>
 *     <li>the header (first line) specifies the keys</li>
 *     <li>so keys are considered column identifiers</li>
 *     <li>each collection associated with a key (column identifier) represents all values of that column</li>
 *   </ul>
 * </p>
 *
 * @since 2.9
 * @author Willi Schoenborn
 */
public final class MultimapConverter implements TypeConverter {

    static final TypeLiteral<Multimap<String, String>> LITERAL = 
        new TypeLiteral<Multimap<String, String>>() { };
        
    private final CsvConverter converter = new CsvConverter();
    
    @Override
    public ListMultimap<String, String> convert(String value, TypeLiteral<?> toType) {
        final List<String[]> lines = converter.convert(value, CsvConverter.LITERAL);
        final String[] header = lines.remove(0);
        
        final ListMultimap<String, String> multimap = LinkedListMultimap.create();
        
        for (String[] line : lines) {
            for (int i = 0; i < header.length; i++) {
                multimap.put(header[i], line[i]);
            }
        }
        
        return multimap;
    }
    
}
