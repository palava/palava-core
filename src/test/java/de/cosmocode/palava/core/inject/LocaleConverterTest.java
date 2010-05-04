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

import java.util.Locale;

import org.junit.Assert;
import org.junit.Test;

import com.google.inject.TypeLiteral;

import de.cosmocode.junit.UnitProvider;

/**
 * Tests {@link LocaleConverter}.
 *
 * @author Willi Schoenborn
 */
public final class LocaleConverterTest implements UnitProvider<LocaleConverter> {

    private static final TypeLiteral<Locale> LITERAL = TypeLiteral.get(Locale.class);

    @Override
    public LocaleConverter unit() {
        return new LocaleConverter();
    }
    
    /**
     * Tests {@link LocaleConverter#convert(String, TypeLiteral)} with en.
     * 
     * @since
     */
    @Test
    public void en() {
        Assert.assertEquals(Locale.ENGLISH, unit().convert("en", LITERAL));
    }
    
    /**
     * Tests {@link LocaleConverter#convert(String, TypeLiteral)} with de_DE.
     * 
     * @since
     */
    @Test
    public void deDE() {
        Assert.assertEquals(Locale.GERMANY, unit().convert("de_DE", LITERAL));
    }
    
    /**
     * Tests {@link LocaleConverter#convert(String, TypeLiteral)} with fr_CA_WIN.
     * 
     * @since
     */
    @Test
    public void frCAWIN() {
        Assert.assertEquals(new Locale("fr", "CA", "WIN"), unit().convert("fr_CA_WIN", LITERAL));
    }
    
    /**
     * Tests {@link LocaleConverter#convert(String, TypeLiteral)} with an illegal input.
     */
    @Test(expected = RuntimeException.class)
    public void illegal() {
        unit().convert("DE", LITERAL);
    }
    
}
