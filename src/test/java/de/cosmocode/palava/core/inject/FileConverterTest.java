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
