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

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

import com.google.inject.TypeLiteral;

import de.cosmocode.junit.UnitProvider;

/**
 * Tests {@link FileConverter}.
 *
 * @author Willi Schoenborn
 */
public final class FileConverterTest implements UnitProvider<FileConverter> {
    
    private static final TypeLiteral<File> LITERAL = TypeLiteral.get(File.class);
    
    @Override
    public FileConverter unit() {
        return new FileConverter();
    }
    
    /**
     * Tests {@link FileConverter#convert(String, TypeLiteral)} with a present file.
     */
    @Test
    public void present() {
        Assert.assertEquals(new File("present.file"), unit().convert("file:present.file", LITERAL));
    }

    /**
     * Tests {@link FileConverter#convert(String, TypeLiteral)} with a missing file.
     */
    @Test
    public void missing() {
        Assert.assertEquals(new File("missing.file"), unit().convert("file:missing.file", LITERAL));
    }
    
    /**
     * Tests {@link FileConverter#convert(String, TypeLiteral)} with an illegal input.
     */
    @Test(expected = RuntimeException.class)
    public void illegal() {
        unit().convert("file.ext", LITERAL);
    }

}
