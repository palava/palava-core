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

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.TypeConverter;


/**
 * A {@link TypeConverter} for {@link Table}s from .csv files.
 * 
 * <p>
 *   This converter relies upon {@link CsvConverter} and maps
 *   the read lines into a table using the following semantics:<br />
 *   <ul>
 *     <li>the header (first line) specifies the column keys</li>
 *     <li>the row keys are the line numbers starting at 0 (header is excluded)</li>
 *     <li>the value is the cell in the csv file of the given column at the specified row</li>
 *   </ul>
 * </p>
 *
 * @since 2.9
 * @author Willi Schoenborn
 */
public final class TableConverter implements TypeConverter {
    
    public static final TypeLiteral<Table<Integer, String, String>> LITERAL = 
        new TypeLiteral<Table<Integer, String, String>>() { };

    private final CsvConverter converter = new CsvConverter();
    
    @Override
    public Table<Integer, String, String> convert(String value, TypeLiteral<?> literal) {
        final List<String[]> lines = converter.convert(value, CsvConverter.LITERAL);
        final String[] header = lines.remove(0);
                
        final Table<Integer, String, String> table = HashBasedTable.create();

        int index = 0;
        for (String[] line : lines) {
            for (int i = 0; i < header.length; i++) {
                table.put(index, header[i], line[i]);
            }
            index++;
        }
                
        return table;
    }
    
}
