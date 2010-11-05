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
 * A simple {@link CsvStrategy}.
 *
 * @since 2.9
 * @author Willi Schoenborn
 */
final class SimpleCsvStrategy implements CsvStrategy {
    
    private final char separator;
    private final char quote;
    private final char escape;

    SimpleCsvStrategy(char separator, char quote, char escape) {
        this.separator = separator;
        this.quote = quote;
        this.escape = escape;
    }

    @Override
    public char separator() {
        return separator;
    }

    @Override
    public char quote() {
        return quote;
    }

    @Override
    public char escape() {
        return escape;
    }

    @Override
    public String toString() {
        return "CsvStrategy [separator='" + separator() + "', quote='" + quote() + "', escape='" + escape() + "']";
    }
    
}
