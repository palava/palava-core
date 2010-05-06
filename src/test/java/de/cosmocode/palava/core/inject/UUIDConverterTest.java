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
