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

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.Multimap;

import de.cosmocode.junit.UnitProvider;

/**
 * Tests {@link MultimapConverter}.
 *
 * @since 2.9
 * @author Willi Schoenborn
 */
public final class MultimapConverterTest implements UnitProvider<MultimapConverter> {

    @Override
    public MultimapConverter unit() {
        return new MultimapConverter();
    }

    private void test(Multimap<String, String> map) {
        Assert.assertEquals(Arrays.asList("1997", "2000"), map.get("Year"));
        Assert.assertEquals(Arrays.asList("Ford", "Mercury"), map.get("Make"));
        Assert.assertEquals(Arrays.asList("E350", "Cougar"), map.get("Model"));
        Assert.assertEquals(Arrays.asList("2.34", "2.38"), map.get("Length"));
    }
    
    /**
     * Tests using default.csv.
     */
    @Test
    public void defaults() {
        final Multimap<String, String> map = unit().convert("csv:classpath:default.csv", MultimapConverter.LITERAL);
        test(map);
    }
    
    /**
     * Tests using default.tsv.
     */
    @Test
    public void tabbed() {
        final Multimap<String, String> map = unit().convert("csv:\t:classpath:default.tsv", MultimapConverter.LITERAL);
        test(map);
    }
    
}
