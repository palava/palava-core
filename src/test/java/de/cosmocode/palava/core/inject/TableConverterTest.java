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

import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import com.google.inject.TypeLiteral;

import de.cosmocode.junit.UnitProvider;

/**
 * Tests {@link TableConverter}.
 *
 * @since 2.9
 * @author Willi Schoenborn
 */
public final class TableConverterTest implements UnitProvider<TableConverter> {

    @Override
    public TableConverter unit() {
        return new TableConverter();
    }
    
    /**
     * Tests using default.csv.
     */
    @Test
    public void defaults() {
        final TypeLiteral<Table<Integer, String, String>> literal = TableConverter.LITERAL;
        final Table<Integer, String, String> table = unit().convert("csv:classpath:default.csv", literal);
        
        Assert.assertEquals(Sets.newHashSet("Year", "Make", "Model", "Length"), table.columnKeySet());
        Assert.assertEquals(Sets.newHashSet(0, 1), table.rowKeySet());
        
        Assert.assertEquals("1997", table.get(0, "Year"));
        Assert.assertEquals("Ford", table.get(0, "Make"));
        Assert.assertEquals("E350", table.get(0, "Model"));
        Assert.assertEquals("2.34", table.get(0, "Length"));

        Assert.assertEquals("2000", table.get(1, "Year"));
        Assert.assertEquals("Mercury", table.get(1, "Make"));
        Assert.assertEquals("Cougar", table.get(1, "Model"));
        Assert.assertEquals("2.38", table.get(1, "Length"));
    }
    
}
