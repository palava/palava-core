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

import java.io.File;
import java.net.InetAddress;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.Properties;
import java.util.UUID;
import java.util.regex.Pattern;

import org.slf4j.Logger;

import com.google.inject.Module;
import com.google.inject.spi.TypeConverter;

/**
 * A {@link Module} for custom {@link TypeConverter}s.
 *
 * @author Willi Schoenborn
 */
public final class TypeConverterModule extends CustomTypeConverterModule {

    @Override
    protected void configure() {
        register(Charset.class, new CharsetConverter());
        register(File.class, new FileConverter());
        register(InetAddress.class, new InetAddressConverter());
        register(Locale.class, new LocaleConverter());
        register(Logger.class, new LoggerConverter());
        register(Pattern.class, new PatternConverter());
        register(Properties.class, new PropertiesConverter());
        register(URI.class, new URIConverter());
        register(URL.class, new URLConverter());
        register(UUID.class, new UUIDConverter());
    }

}
