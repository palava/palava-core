package de.cosmocode.palava.core.inject;

import java.util.UUID;

import com.google.inject.spi.TypeConverter;

/**
 * {@link TypeConverter} for {@link UUID}s.
 *
 * @author Willi Schoenborn
 */
public final class UUIDConverter extends AbstractTypeConverter<UUID> {

    @Override
    public UUID convert(String value) {
        return UUID.fromString(value);
    }
    
}
