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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package de.cosmocode.palava.bridge.simple.content;

import com.google.common.base.Preconditions;

import de.cosmocode.patterns.Immutable;

/**
 * MimeTypes define a content type.
 * 
 * @author Detlef Hüttemann
 * @author Willi Schoenborn
 */
@Immutable
public class MimeType {
    
    public static final MimeType ERROR = new MimeType("application/error");
    public static final MimeType PHP = new MimeType("application/x-httpd-php");
    public static final MimeType JSON = new MimeType("application/json");
    public static final MimeType TEXT = new MimeType("text/plain");
    public static final MimeType XML = new MimeType("application/xml");
    public static final MimeType HTML = new MimeType("text/html");
    public static final MimeType IMAGE = new MimeType("image/*");
    public static final MimeType JPEG = new MimeType("image/jpeg");
    
    private final String type;

    public MimeType(String type) {
        this.type = Preconditions.checkNotNull(type, "MimeType");
        final int slash = type.indexOf("/");
        Preconditions.checkArgument(slash >= 0, "Type contains no /");
        Preconditions.checkArgument(slash != type.length() - 1, "Type ends with /");
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof MimeType)) {
            return false;
        }
        final MimeType other = (MimeType) obj;
        if (type == null) {
            if (other.type != null) {
                return false;
            }
        } else if (!type.equals(other.type)) {
            return false;
        }
        return true;
    }

    /**
     * Returns the part after the '/'.
     * 
     * @return the minor part
     */
    public String getMinor() {
        return type.substring(type.indexOf("/") + 1);
    }
    
    @Override
    public String toString() {
        return type;
    }

}
