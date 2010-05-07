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

import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;

import com.google.inject.TypeLiteral;

import de.cosmocode.junit.UnitProvider;

/**
 * Tests {@link UUIDConverter}.
 *
 * @since 2.4
 * @author Willi Schoenborn
 */
public final class UUIDConverterTest implements UnitProvider<UUIDConverter> {

    private static final TypeLiteral<UUID> LITERAL = TypeLiteral.get(UUID.class);
    
    @Override
    public UUIDConverter unit() {
        return new UUIDConverter();
    }
    
    /**
     * Tests {@link UUIDConverter#convert(String, TypeLiteral)} with a valid uuid.
     */
    @Test
    public void valid() {
        final UUID uuid = UUID.randomUUID();
        Assert.assertEquals(uuid, unit().convert(uuid.toString(), LITERAL));
    }
    
    /**
     * Tests {@link UUIDConverter#convert(String, TypeLiteral)} with an invalid uuid.
     */
    @Test(expected = RuntimeException.class)
    public void invalid() {
        unit().convert("", LITERAL);
    }

}
