/**
 * palava - a java-php-bridge
 * Copyright (C) 2007-2010  CosmoCode GmbH
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
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
