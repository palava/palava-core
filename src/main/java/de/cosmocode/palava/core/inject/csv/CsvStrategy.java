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

import javax.annotation.concurrent.Immutable;

/**
 * A value object for different special chars, required
 * during csv parsing.
 *
 * @since 2.9
 * @author Willi Schoenborn
 */
@Immutable
interface CsvStrategy {

    /**
     * Provides the separator char.
     *
     * @since 2.9
     * @return the separator
     */
    char separator();
    
    /**
     * Provides the quote char.
     *
     * @since 2.9
     * @return the quote char
     */
    char quote();
    
    /**
     * Provides the escape char.
     *
     * @since 2.9
     * @return the escape char
     */
    char escape();
    
}
