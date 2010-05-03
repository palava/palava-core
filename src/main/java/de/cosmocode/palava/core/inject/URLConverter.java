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

import java.net.MalformedURLException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.inject.spi.TypeConverter;

/**
 * A {@link TypeConverter} for {@link URL}s.
 * 
 * @since 2.4
 * @author Willi Schoenborn
 */
public final class URLConverter extends AbstractTypeConverter<URL> {

    private static final Logger LOG = LoggerFactory.getLogger(URLConverter.class);
    
    private static final String CLASSPATH_PREFIX = "classpath:";

    @Override
    protected URL convert(String value) {
        if (value.startsWith(CLASSPATH_PREFIX)) {
            LOG.trace("Considering {} to be a classpath resource", value);
            final ClassLoader loader = getClassLoader();
            final URL url = loader.getResource(value.substring(CLASSPATH_PREFIX.length()));
            Preconditions.checkArgument(url != null, "%s is not pointing to a valid properties source", value);
            return url;
        } else {
            try {
                return new URL(value);
            } catch (MalformedURLException e) {
                throw new IllegalArgumentException(e);
            }
        }
    }
    
    private ClassLoader getClassLoader() {
        try {
            return Thread.currentThread().getContextClassLoader();
        } catch (SecurityException e) {
            return URLConverter.class.getClassLoader();
        }
    }
    
}
