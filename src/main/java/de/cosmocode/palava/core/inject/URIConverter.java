package de.cosmocode.palava.core.inject;

import java.net.URI;
import java.net.URISyntaxException;

import com.google.inject.spi.TypeConverter;

/**
 * {@link TypeConverter} for {@link URI}s.
 *
 * @author Willi Schoenborn
 */
public final class URIConverter extends AbstractTypeConverter<URI> {

    @Override
    public URI convert(String value) {
        try {
            return new URI(value);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }

}
