package de.cosmocode.palava.core.inject;

import java.util.Locale;

import com.google.inject.spi.TypeConverter;

import de.cosmocode.commons.Locales;

/**
 * {@link TypeConverter} for {@link Locale}s.
 *
 * @author Willi Schoenborn
 */
public final class LocaleConverter extends AbstractTypeConverter<Locale> {

    @Override
    public Locale convert(String value) {
        return Locales.parse(value);
    }

}
