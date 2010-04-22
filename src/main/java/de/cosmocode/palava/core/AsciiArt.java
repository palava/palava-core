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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A utility class for printing the palava ascii art.
 *
 * @author Willi Schoenborn
 */
final class AsciiArt {
    
    private static final Logger LOG = LoggerFactory.getLogger(AsciiArt.class);

    private static final String FILE_NAME = "palava-ascii.txt";
    
    private AsciiArt() {
        
    }

    private static InputStream open() throws IOException {
        final File file = new File("lib", FILE_NAME);
        final URL resource = AsciiArt.class.getClassLoader().getResource(FILE_NAME);
        
        if (file.exists()) {
            return new FileInputStream(file);
        } else {
            if (resource == null) {
                throw new FileNotFoundException(String.format("classpath:%s", FILE_NAME)); 
            }
            return resource.openStream();
        }
    }
    
    /**
     * Prints the palava ascii art by first checking the file lib/palava-ascii.txt
     * and in case it does not exist uses the classpath resource palava-ascii.txt.
     * Prints a warning if none exists.
     */
    public static void print() {
        try {
            final InputStream stream = open();
            final String message = IOUtils.toString(stream, "UTF-8");
            LOG.info("Welcome to\n{}", message);
        } catch (IOException e) {
            LOG.warn("Unable to print ascii art", e);
        }
    }
    
}
