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

import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Test;

import com.google.inject.TypeLiteral;

import de.cosmocode.junit.UnitProvider;

/**
 * Tests {@link PatternConverter}.
 *
 * @author Willi Schoenborn
 */
public final class PatternConverterTest implements UnitProvider<PatternConverter> {

    private static final TypeLiteral<Pattern> LITERAL = TypeLiteral.get(Pattern.class);

    @Override
    public PatternConverter unit() {
        return new PatternConverter();
    }
    
    /**
     * Tests {@link PatternConverter#convert(String, TypeLiteral)} with ".*".
     */
    @Test
    public void all() {
        final Pattern actual = Pattern.class.cast(unit().convert(".*", LITERAL));
        Assert.assertEquals(Pattern.compile(".*").pattern(), actual.pattern());
    }

    /**
     * Tests {@link PatternConverter#convert(String, TypeLiteral)} with ".".
     */
    @Test
    public void any() {
        final Pattern actual = Pattern.class.cast(unit().convert(".", LITERAL));
        Assert.assertEquals(Pattern.compile(".").pattern(), actual.pattern());
    }

    /**
     * Tests {@link PatternConverter#convert(String, TypeLiteral)} with "^.$".
     */
    @Test
    public void only() {
        final Pattern actual = Pattern.class.cast(unit().convert("^.$", LITERAL));
        Assert.assertEquals(Pattern.compile("^.$").pattern(), actual.pattern());
    }
    
    /**
     * Tests {@link PatternConverter#convert(String, TypeLiteral)} with an illegal input.
     */
    @Test(expected = RuntimeException.class)
    public void illegal() {
        unit().convert("**");
    }
    
}
