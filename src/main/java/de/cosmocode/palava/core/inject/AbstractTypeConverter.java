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

import com.google.inject.TypeLiteral;
import com.google.inject.spi.TypeConverter;

/**
 * Abstract base class for {@link TypeConverter}s.
 *
 * @deprecated is not adding any value
 * @since 2.4
 * @param <T> target type
 * @author Willi Schoenborn
 */
@Deprecated
public abstract class AbstractTypeConverter<T> implements TypeConverter {
    
    /**
     * Converts the specified value to type T.
     * 
     * @param value the string value
     * @return value converted to T
     * @throws IllegalArgumentException if conversion failed
     */
    protected abstract T convert(String value);
    
    @Override
    public final Object convert(String value, TypeLiteral<?> typeLiteral) {
        return convert(value);
    }
    
    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
    
}
