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
import java.io.InputStreamReader;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.io.InputSupplier;
import com.google.common.io.Resources;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.TypeConverter;

import de.cosmocode.commons.io.CloseableIterator;
import de.cosmocode.palava.core.inject.URLConverter;

/**
 * A {@link TypeConverter} which.
 *
 * @since 2.9
 * @author Willi Schoenborn
 */
public final class CsvIteratorConverter implements TypeConverter {

    public static final TypeLiteral<CloseableIterator<String[]>> LITERAL = 
        new TypeLiteral<CloseableIterator<String[]>>() { };
    
    static final Pattern PATTERN = Pattern.compile("csv(?::(.))?(?::(.))?(?::(.))?:([^:].+)");
    
    private static final Logger LOG = LoggerFactory.getLogger(CsvConverter.class);
    
    private final URLConverter converter = new URLConverter();
    
    @Override
    public CloseableIterator<String[]> convert(String value, TypeLiteral<?> toType) {
        final Matcher matcher = PATTERN.matcher(value);
        Preconditions.checkArgument(matcher.matches(), "%s does not match %s", value, PATTERN.pattern());
        
        final char separator = extractChar(matcher, 1, DefaultCsvStrategy.INSTANCE.separator());
        final char quote = extractChar(matcher, 2, DefaultCsvStrategy.INSTANCE.quote());
        final char escape = extractChar(matcher, 3, DefaultCsvStrategy.INSTANCE.escape());
        final String location = matcher.group(4);
        
        final URL url = converter.convert(location, URLConverter.LITERAL);
        
        final InputSupplier<InputStreamReader> supplier = Resources.newReaderSupplier(url, Charsets.UTF_8);
        final CsvStrategy strategy = CsvStrategies.of(separator, quote, escape);

        LOG.trace("Reading csv from {}, using {}", location, strategy); 
        
        try {
            // caller is responsible to close the stream
            return new CsvReaderIterator(supplier, strategy);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }
    
    private char extractChar(Matcher matcher, int group, char defaultValue) {
        final String s = matcher.group(group);
        if (s == null) {
            return defaultValue;
        } else {
            // guarded by regex
            assert s.length() == 1;
            return s.charAt(0);
        }
    }
    

}
