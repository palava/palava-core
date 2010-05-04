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

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.io.Resources;
import com.google.inject.TypeLiteral;

import de.cosmocode.junit.UnitProvider;

/**
 * Tests {@link URLConverter}.
 *
 * @author Willi Schoenborn
 */
public final class URLConverterTest implements UnitProvider<URLConverter> {

    private static final TypeLiteral<URL> LITERAL = TypeLiteral.get(URL.class);
    
    @Override
    public URLConverter unit() {
        return new URLConverter();
    }

    /**
     * Tests {@link URLConverter#convert(String, TypeLiteral)} with a http url.
     * 
     * @throws MalformedURLException should not happen 
     */
    @Test
    public void http() throws MalformedURLException {
        Assert.assertEquals(new URL("http://www.google.de"), unit().convert("http://www.google.de", LITERAL));
    }

    /**
     * Tests {@link URLConverter#convert(String, TypeLiteral)} with a ftp url.
     * 
     * @throws MalformedURLException should not happen 
     */
    @Test
    public void ftp() throws MalformedURLException {
        Assert.assertEquals(new URL("ftp://google.de"), unit().convert("ftp://google.de", LITERAL));
    }

    /**
     * Tests {@link URLConverter#convert(String, TypeLiteral)} with a file url.
     * 
     * @throws MalformedURLException should not happen 
     */
    @Test
    public void file() throws MalformedURLException {
        Assert.assertEquals(new URL("file:some.file"), unit().convert("file:some.file", LITERAL));
    }

    /**
     * Tests {@link URLConverter#convert(String, TypeLiteral)} with a classpath url.
     * 
     * @throws MalformedURLException should not happen 
     */
    @Test
    public void classpath() throws MalformedURLException {
        final Object actual = unit().convert("classpath:present.file", LITERAL);
        Assert.assertNotNull(actual);
        Assert.assertEquals(Resources.getResource("present.file"), actual);
    }
    
    /**
     * Tests {@link URLConverter#convert(String, TypeLiteral)} with an illegal input.
     */
    @Test(expected = RuntimeException.class)
    public void illegal() {
        unit().convert("", LITERAL);
    }
    
}
