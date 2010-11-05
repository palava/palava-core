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

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import de.cosmocode.junit.UnitProvider;

/**
 * Tests {@link CsvConverter}.
 *
 * @since 2.9
 * @author Willi Schoenborn
 */
public final class CsvConverterTest implements UnitProvider<CsvConverter> {

    @Override
    public CsvConverter unit() {
        return new CsvConverter();
    }
    
    private void test(List<String[]> lines) {
        final String[] header = lines.get(0);
        
        Assert.assertEquals("Year", header[0]);
        Assert.assertEquals("Make", header[1]);
        Assert.assertEquals("Model", header[2]);
        Assert.assertEquals("Length", header[3]);
        
        final String[] ford = lines.get(1);

        Assert.assertEquals("1997", ford[0]);
        Assert.assertEquals("Ford", ford[1]);
        Assert.assertEquals("E350", ford[2]);
        Assert.assertEquals("2.34", ford[3]);
        
        final String[] mercury = lines.get(2);

        Assert.assertEquals("2000", mercury[0]);
        Assert.assertEquals("Mercury", mercury[1]);
        Assert.assertEquals("Cougar", mercury[2]);
        Assert.assertEquals("2.38", mercury[3]);
    }
    
    /**
     * Tests using default.csv.
     */
    @Test
    public void defaults() {
        final List<String[]> lines = unit().convert("csv:classpath:default.csv", CsvConverter.LITERAL);
        test(lines);
    }
    
    /**
     * Tests using default.tsv.
     */
    @Test
    public void tabbed() {
        final List<String[]> lines = unit().convert("csv:\t:classpath:default.tsv", CsvConverter.LITERAL);
        test(lines);
    }
    
}
