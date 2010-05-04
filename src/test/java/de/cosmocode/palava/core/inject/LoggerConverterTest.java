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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.TypeLiteral;

import de.cosmocode.junit.UnitProvider;

/**
 * Tests {@link LoggerConverter}.
 *
 * @author Willi Schoenborn
 */
public final class LoggerConverterTest implements UnitProvider<LoggerConverter> {

    private static final TypeLiteral<Logger> LITERAL = TypeLiteral.get(Logger.class);
    
    @Override
    public LoggerConverter unit() {
        return new LoggerConverter();
    }
    
    /**
     * Tests {@link LoggerConverter#convert(String, TypeLiteral)} with a valid category.
     */
    @Test
    public void valid() {
        Assert.assertEquals(LoggerFactory.getLogger("com.company.LOG"), unit().convert("com.company.LOG", LITERAL));
    }
    
}
