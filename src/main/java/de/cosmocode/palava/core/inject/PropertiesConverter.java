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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import org.apache.commons.io.IOUtils;

import com.google.inject.spi.TypeConverter;

/**
 * {@link TypeConverter} for {@link Properties}.
 *
 * @since 2.4
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
