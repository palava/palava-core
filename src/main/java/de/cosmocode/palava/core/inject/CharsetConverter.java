package de.cosmocode.palava.core.inject;

import java.nio.charset.Charset;

import com.google.inject.spi.TypeConverter;

/**
 * {@link TypeConverter} for {@link Charset}s.
 *
 * @author Willi Schoenborn
 */
public final class CharsetConverter extends AbstractTypeConverter<Charset> {

    @Override
    public Charset convert(String value) {
        return Charset.forName(value);
    }

}
