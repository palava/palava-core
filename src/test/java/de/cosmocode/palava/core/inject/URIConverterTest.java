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

import java.net.URI;
import java.net.URISyntaxException;

import org.junit.Assert;
import org.junit.Test;

import com.google.inject.TypeLiteral;

import de.cosmocode.junit.UnitProvider;

/**
 * Tests {@link URIConverter}.
 *
 * @since 2.4
 * @author Willi Schoenborn
 */
public final class URIConverterTest implements UnitProvider<URIConverter> {

    private static final TypeLiteral<URI> LITERAL = TypeLiteral.get(URI.class);

    @Override
    public URIConverter unit() {
        return new URIConverter();
    }
    
    /**
     * Tests {@link URIConverter#convert(String, TypeLiteral)} with a http uri.
     * 
     * @throws URISyntaxException should not happen 
     */
    @Test
    public void http() throws URISyntaxException {
        Assert.assertEquals(new URI("http://www.google.de"), unit().convert("http://www.google.de", LITERAL));
    }

    /**
     * Tests {@link URLConverter#convert(String, TypeLiteral)} with a ftp url.
     * 
     * @throws URISyntaxException should not happen 
     */
    @Test
    public void ftp() throws URISyntaxException {
        Assert.assertEquals(new URI("ftp://google.de"), unit().convert("ftp://google.de", LITERAL));
    }

    /**
     * Tests {@link URIConverter#convert(String, TypeLiteral)} with a file URI.
     * 
     * @throws URISyntaxException should not happen 
     */
    @Test
    public void file() throws URISyntaxException {
        Assert.assertEquals(new URI("file:some.file"), unit().convert("file:some.file", LITERAL));
    }

}
