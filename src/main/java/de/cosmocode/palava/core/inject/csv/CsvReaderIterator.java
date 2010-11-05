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

import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

import au.com.bytecode.opencsv.CSVReader;

import com.google.common.base.Preconditions;
import com.google.common.io.InputSupplier;

import de.cosmocode.commons.io.CloseableIterator;
import de.cosmocode.patterns.Adapter;

/**
 * An adapter from {@link CSVReader} to {@link Iterator}.
 *
 * @since 2.9
 * @author Willi Schoenborn
 */
@Adapter(Iterator.class)
final class CsvReaderIterator implements CloseableIterator<String[]> {

    private final CSVReader reader;
    private final String[] header;
    
    private String[] next;
    private int lineNumber;
    
    CsvReaderIterator(InputSupplier<? extends Reader> supplier, CsvStrategy strategy) throws IOException {
        Preconditions.checkNotNull(supplier, "Supplier");
        Preconditions.checkNotNull(strategy, "Strategy");
        
        final Reader input = Preconditions.checkNotNull(supplier.getInput(), "%s.getInput()", supplier);
        this.reader = new CSVReader(input, strategy.separator(), strategy.quote(), strategy.escape());
        
        this.header = readNext();
        Preconditions.checkArgument(header != null, "%s is empty", supplier);
        this.next = header;
    }
    
    private String[] readNext() {
        try {
            return reader.readNext();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
    
    @Override
    public boolean hasNext() {
        return next != null;
    }

    @Override
    public String[] next() {
        if (hasNext()) {
            final String[] current = extendIfNecessary(next);
            next = readNext();
            lineNumber++;
            return current;
        } else {
            throw new NoSuchElementException();
        }
    }
    
    private String[] extendIfNecessary(String[] line) {
        if (line.length == header.length) {
            return line;
        } else if (line.length < header.length) {
            final String[] extended = new String[header.length];
            Arrays.fill(extended, "");
            System.arraycopy(line, 0, extended, 0, line.length);
            return extended;
        } else {
            throw new IllegalArgumentException(String.format("Line #%s is too long", lineNumber));
        }
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void close() throws IOException {
        reader.close();
    }
    
}
