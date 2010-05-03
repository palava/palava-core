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

import java.nio.charset.Charset;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.inject.TypeLiteral;

import de.cosmocode.junit.UnitProvider;

/**
 * Tests {@link CharsetConverter}.
 *
 * @author Willi Schoenborn
 */
public final class CharsetConverterTest implements UnitProvider<CharsetConverter> {
    
    private static final TypeLiteral<Charset> LITERAL = TypeLiteral.get(Charset.class);

    @Override
    public CharsetConverter unit() {
        return new CharsetConverter();
    }

    /**
     * Tests {@link CharsetConverter#convert(String, TypeLiteral)} with "UTF-8".
     */
    @Test
    public void utf8() {
        Assert.assertEquals(Charsets.UTF_8, unit().convert("UTF-8", LITERAL));
    }
    
    /**
     * Tests {@link CharsetConverter#convert(String, TypeLiteral)} with "UTF-16".
     */
    @Test
    public void utf16() {
        Assert.assertEquals(Charsets.UTF_16, unit().convert("UTF-16", LITERAL));
    }
    
    /**
     * Tests {@link CharsetConverter#convert(String, TypeLiteral)} with "ascii".
     */
    @Test
    public void ascii() {
        Assert.assertEquals(Charsets.US_ASCII, unit().convert("ascii", LITERAL));
    }

    /**
     * Tests {@link CharsetConverter#convert(String, TypeLiteral)} with "unknown".
     */
    @Test(expected = RuntimeException.class)
    public void unknown() {
        unit().convert("unknown", LITERAL);
    }
    
}
