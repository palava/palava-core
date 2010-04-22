package de.cosmocode.palava.core.inject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import org.apache.commons.io.IOUtils;

import com.google.inject.spi.TypeConverter;

/**
 * {@link TypeConverter} for {@link Properties}.
 *
 * @author Willi Schoenborn
 */
public final class PropertiesConverter extends AbstractTypeConverter<Properties> {

    private final URLConverter urlConverter = new URLConverter();
    
    @Override
    public Properties convert(String value) {
        final URL url = urlConverter.convert(value);
        final Properties properties = new Properties();
        final InputStream stream = openStream(url);
        
        try {
            properties.load(stream);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        } finally {
            IOUtils.closeQuietly(stream);
        }
        
        return properties;
    }
    
    private InputStream openStream(URL url) {
        try {
            return url.openStream();
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

}
