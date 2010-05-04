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

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.junit.Assert;
import org.junit.Test;

import com.google.inject.TypeLiteral;

import de.cosmocode.junit.UnitProvider;

/**
 * Tests {@link InetAddressConverter}.
 *
 * @author Willi Schoenborn
 */
public final class InetAddressConverterTest implements UnitProvider<InetAddressConverter> {

    public static final TypeLiteral<InetAddress> LITERAL = TypeLiteral.get(InetAddress.class);
    
    @Override
    public InetAddressConverter unit() {
        return new InetAddressConverter();
    }
    
    /**
     * Tests {@link InetAddressConverter#convert(String, TypeLiteral)} with localhost.
     * 
     * @throws UnknownHostException should not happen 
     */
    @Test 
    public void localhost() throws UnknownHostException {
        Assert.assertEquals(InetAddress.getByName("localhost"), unit().convert("localhost", LITERAL));
    }

    /**
     * Tests {@link InetAddressConverter#convert(String, TypeLiteral)} with 127.0.0.1.
     * 
     * @throws UnknownHostException should not happen 
     */
    @Test
    public void loopback() throws UnknownHostException {
        Assert.assertEquals(InetAddress.getByAddress(new byte[] {127, 0, 0, 1}), unit().convert("127.0.0.1", LITERAL));
    }

    /**
     * Tests {@link InetAddressConverter#convert(String, TypeLiteral)} with 8.8.8.8.
     * 
     * @throws UnknownHostException should not happen 
     */
    @Test
    public void google() throws UnknownHostException {
        Assert.assertEquals(InetAddress.getByAddress(new byte[] {8, 8, 8, 8}), unit().convert("8.8.8.8", LITERAL));
    }

    /**
     * Tests {@link InetAddressConverter#convert(String, TypeLiteral)} with an illegal address.
     */
    @Test(expected = RuntimeException.class)
    public void illegal() {
        unit().convert("192.168.0.x", LITERAL);
    }
    
}
