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

import java.net.InetSocketAddress;

import org.junit.Assert;
import org.junit.Test;

import com.google.inject.TypeLiteral;

import de.cosmocode.junit.UnitProvider;

/**
 * Tests {@link InetSocketAddressConverter}.
 *
 * @since 2.4 
 * @author Willi Schoenborn
 */
public final class InetSocketAddressConverterTest implements UnitProvider<InetSocketAddressConverter> {

    private static final TypeLiteral<InetSocketAddress> LITERAL = TypeLiteral.get(InetSocketAddress.class);

    @Override
    public InetSocketAddressConverter unit() {
        return new InetSocketAddressConverter();
    }
    
    /**
     * Tests {@link InetSocketAddressConverter#convert(String, TypeLiteral)} with a valid input (hostname:8080).
     */
    @Test
    public void hostname() {
        Assert.assertEquals(new InetSocketAddress("localhost", 8080), 
            unit().convert("localhost:8080", LITERAL));
    }

    /**
     * Tests {@link InetSocketAddressConverter#convert(String, TypeLiteral)} with a valid input (127.0.0.1:8080).
     */
    @Test
    public void loopback() {
        Assert.assertEquals(new InetSocketAddress("127.0.0.1", 8080), 
            unit().convert("127.0.0.1:8080", LITERAL));
    }


    /**
     * Tests {@link InetSocketAddressConverter#convert(String, TypeLiteral)} with a valid input (0.0.0.0:8080).
     */
    @Test
    public void all() {
        Assert.assertEquals(new InetSocketAddress("0.0.0.0", 8080), 
            unit().convert("0.0.0.0:8080", LITERAL));
    }
    
    /**
     * Tests {@link InetSocketAddressConverter#convert(String, TypeLiteral)} with a valid input (*:8080).
     * 
     * @since
     */
    @Test
    public void wildcard() {
        Assert.assertEquals(new InetSocketAddress(8080),
            unit().convert("*:8080", LITERAL));
    }
    
    /**
     * Tests {@link InetSocketAddressConverter#convert(String, TypeLiteral)} without port.
     */
    @Test(expected = IllegalArgumentException.class)
    public void noPort() {
        unit().convert("localhost", LITERAL);
    }
    
    /**
     * Tests {@link InetSocketAddressConverter#convert(String, TypeLiteral)} without a host.
     */
    @Test(expected = IllegalArgumentException.class)
    public void noHost() {
        unit().convert(":8080", LITERAL);
    }

    /**
     * Tests {@link InetSocketAddressConverter#convert(String, TypeLiteral)} with a single port.
     */
    @Test(expected = IllegalArgumentException.class)
    public void port() {
        unit().convert("8080", LITERAL);
    }
    
}
