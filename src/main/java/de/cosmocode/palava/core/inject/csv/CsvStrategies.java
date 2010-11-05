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

/**
 * Static utility class for {@link CsvStrategy csv strategies}.
 *
 * @since 2.9
 * @author Willi Schoenborn
 */
final class CsvStrategies {
    
    private CsvStrategies() {
        
    }

    /**
     * Provides the default {@link CsvStrategy}, which
     * uses ',', '"' and '\' as separator, quote and escape
     * respectively.
     *
     * @since 2.9
     * @return the default csv strategy
     */
    public static CsvStrategy defaults() {
        return DefaultCsvStrategy.INSTANCE;
    }
    
    /**
     * Creates a {@link CsvStrategy} from the given chars.
     *
     * @since 2.9
     * @param separator the separator char
     * @param quote the quote char
     * @param escape the escape char
     * @return a new csv strategy
     */
    public static CsvStrategy of(char separator, char quote, char escape) {
        return new SimpleCsvStrategy(separator, quote, escape);
    }
    
}
