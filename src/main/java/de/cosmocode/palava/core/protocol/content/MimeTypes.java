/**
 * palava - a java-php-bridge
 * Copyright (C) 2007  CosmoCode GmbH
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package de.cosmocode.palava.core.protocol.content;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * can parse the mimetype from a file extension.
 * 
 * @author Detlef Hüttemann
 */
public final class MimeTypes {

    public static final MimeTypes SINGLETON;
    
    static {
        try {
            SINGLETON = new MimeTypes("/etc/mime.types");
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    };

    private final Map<String, MimeType> mimeTypes;

    private MimeTypes(String file) throws IOException {
        mimeTypes = new HashMap<String, MimeType>();
        final BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;
        final Pattern pattern = Pattern.compile("\\s+");

        while (null != (line = reader.readLine())) {
            if (line.length() > 0 && line.charAt(0) != '#') {
                final String [] results = pattern.split(line);
                if (results.length > 1) {
                    final MimeType mimeType = new MimeType(results[0]);
                    for (String name : results) {
                        mimeTypes.put(name, mimeType);
                    }
                }
            }
        }
    }

    /**
     * Get a {@link MimeType} by file extension.
     * 
     * @param ext the file extension
     * @return the found {@link MimeType} or null if there was no mimetype
     *         found for the given extension
     */
    public MimeType byExtension(String ext) {
        return mimeTypes.get(ext);
    }
    
    /**
     * Get a {@link MimeType} by name.
     * 
     * @param name the name
     * @return the found {@link MimeType} of null if there was no mimetype
     *         found for the given name
     */
    public MimeType byName(String name) {
        final int dot = name.lastIndexOf(".");
        if (dot != -1) 
            return byExtension(name.substring(dot + 1).toLowerCase());
        return null;
    }

}
