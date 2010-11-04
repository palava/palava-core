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

import java.util.regex.Matcher;

import junit.framework.Assert;

import org.junit.Test;

import de.cosmocode.junit.Asserts;

/**
 * Tests {@link CsvConverter#PATTERN}.
 *
 * @since 2.9
 * @author Willi Schoenborn
 */
public final class CsvConverterPatternTest {

    /**
     * Tests without any configuration.
     */
    @Test
    public void noConfig() {
        final Matcher matcher = CsvConverter.PATTERN.matcher("csv:file://test.csv");
        Assert.assertTrue("does not match", matcher.matches());
        Assert.assertNull(matcher.group(1));
        Assert.assertNull(matcher.group(2));
        Assert.assertNull(matcher.group(3));
        Assert.assertEquals("file://test.csv", matcher.group(4));
    }
    
    /**
     * Tests with separator.
     */
    @Test
    public void separator() {
        final Matcher matcher = CsvConverter.PATTERN.matcher("csv:\t:file://test.csv");
        Assert.assertTrue("does not match", matcher.matches());
        Assert.assertEquals("\t", matcher.group(1));
        Assert.assertNull(matcher.group(2));
        Assert.assertNull(matcher.group(3));
        Assert.assertEquals("file://test.csv", matcher.group(4));
    }
    
    /**
     * Tests with separator and quote.
     */
    @Test
    public void separatorAndQuote() {
        final Matcher matcher = CsvConverter.PATTERN.matcher("csv:,:\":file://test.csv");
        Assert.assertTrue("does not match", matcher.matches());
        Assert.assertEquals(",", matcher.group(1));
        Assert.assertEquals("\"", matcher.group(2));
        Assert.assertNull(matcher.group(3));
        Assert.assertEquals("file://test.csv", matcher.group(4));
    }
    
    /**
     * Tests with separator, quote and escape.
     */
    @Test
    public void separatorQuoteAndEscape() {
        final Matcher matcher = CsvConverter.PATTERN.matcher("csv:,:\":\\:file://test.csv");
        Assert.assertTrue("does not match", matcher.matches());
        Assert.assertEquals(",", matcher.group(1));
        Assert.assertEquals("\"", matcher.group(2));
        Assert.assertEquals("\\", matcher.group(3));
        Assert.assertEquals("file://test.csv", matcher.group(4));
    }
    
    /**
     * Tests with invalid inputs.
     */
    @Test
    public void invalid() {
        Asserts.assertDoesNotMatch(CsvConverter.PATTERN, "");
        Asserts.assertDoesNotMatch(CsvConverter.PATTERN, "csv");
        Asserts.assertDoesNotMatch(CsvConverter.PATTERN, "csv:");
        Asserts.assertDoesNotMatch(CsvConverter.PATTERN, "cvs:file://test.csv");
        Asserts.assertDoesNotMatch(CsvConverter.PATTERN, "csv::http://www.example.com/data.csv");
        Asserts.assertDoesNotMatch(CsvConverter.PATTERN, "test.csv");
        Asserts.assertDoesNotMatch(CsvConverter.PATTERN, ",:ftp://ftp.example.org/data.csv");
    }
    
}
