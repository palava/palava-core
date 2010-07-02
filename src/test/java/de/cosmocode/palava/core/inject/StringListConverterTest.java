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

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.google.inject.TypeLiteral;

import de.cosmocode.junit.UnitProvider;

/**
 * Tests {@link StringListConverter}.
 *
 * @since 2.6
 * @author Willi Schoenborn
 */
public final class StringListConverterTest implements UnitProvider<StringListConverter> {

    private static final TypeLiteral<List<String>> LITERAL = new TypeLiteral<List<String>>() { };
    
    @Override
    public StringListConverter unit() {
        return new StringListConverter();
    }

    /**
     * Tests {@link StringListConverter#convert(String, TypeLiteral)} using plain.list.
     */
    @Test
    public void plain() {
        final String path = "file:src/test/resources/plain.list";
        Assert.assertEquals(Arrays.asList("plain", "text", "entries"), unit().convert(path, LITERAL));
    }
    
    /**
     * Tests {@link StringListConverter#convert(String, TypeLiteral)} using commented.list.
     */
    @Test
    public void commented() {
        final String path = "file:src/test/resources/commented.list";
        final List<String> expected = Arrays.asList(
            "entries with", 
            "inline comments", 
            "blank lines", 
            "and spaces before and after"
        );
        Assert.assertEquals(expected, unit().convert(path, LITERAL));
        
    }
    
}
