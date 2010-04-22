package de.cosmocode.palava.core.inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.spi.TypeConverter;

/**
 * {@link TypeConverter} for {@link Logger}s.
 *
 * @author Willi Schoenborn
 */
public final class LoggerConverter extends AbstractTypeConverter<Logger> {

    @Override
    public Logger convert(String value) {
        return LoggerFactory.getLogger(value);
    }

}
