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

package de.cosmocode.palava.core;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

/**
 * Static utility class for test environments.
 *
 * @author Willi Schoenborn
 */
public final class FrameworkLoader {

    private static final Logger LOG = LoggerFactory.getLogger(FrameworkLoader.class);
    
    private FrameworkLoader() {
        
    }

    /**
     * Load properties from either {@code src/test/resources/application.properties}
     * or {@code classath:application.properties} and creates a new {@link Framework}
     * using {@link Palava#newFramework(Properties)}.
     * 
     * @return a new configured framwork
     */
    public static Framework load() {
        final File file = new File("src/test/resources/application.properties");
        final Properties properties = new Properties();
        
        if (file.exists()) {
            LOG.info("Using {}", file);
            try {
                final Reader reader = new FileReader(file);
                try {
                    properties.load(reader);
                } finally {
                    reader.close();
                }
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        } else {
            LOG.info("{} does not exist, looking for classpath resources", file);
            final ClassLoader loader = FrameworkLoader.class.getClassLoader();
            final InputStream stream = loader.getResourceAsStream("application.properties");
            Preconditions.checkState(stream != null, "No application.properties found");
            try {
                try {
                    properties.load(stream);
                } finally {
                    stream.close();
                }
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }
        
        return Palava.newFramework(properties);
    }

}
