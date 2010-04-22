package de.cosmocode.palava.core.inject;

import java.util.regex.Pattern;

import com.google.inject.spi.TypeConverter;

/**
 * {@link TypeConverter} for {@link Pattern}s.
 *
 * @author Willi Schoenborn
 */
public final class PatternConverter extends AbstractTypeConverter<Pattern> {

    @Override
    public Pattern convert(String value) {
        return Pattern.compile(value);
    }

}
