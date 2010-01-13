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

package de.cosmocode.palava.core.protocol;

import java.io.IOException;
import java.io.OutputStream;

import de.cosmocode.palava.ConversionException;
import de.cosmocode.palava.MimeType;
import de.cosmocode.palava.PHPConverter;

/**
 * Content which uses PHP notation.
 * 
 * @author Detlef Hüttemann
 * @author Willi Schoenborn
 * @deprecated use {@link JSONContent} instead
 */
@Deprecated
public class PHPContent extends AbstractContent {
    
    public static final PHPContent OK;
    public static final PHPContent NOT_FOUND;
    
    static {
        try {
            OK = new PHPContent("ok");
            NOT_FOUND = new PHPContent("not_found");
        } catch (ConversionException e) {
            throw new ExceptionInInitializerError(e);
        }
    };
    
    private final byte [] bytes;
    
    public PHPContent(Object object) throws ConversionException {
        super(MimeType.PHP);
        final PHPConverter converter = new PHPConverter();
        final StringBuffer buf = new StringBuffer();
        converter.convert(buf, object);
        bytes = buf.toString().getBytes();
    }
    
    @Override
    public long getLength() {
        return bytes.length;
    }
    
    @Override
    public void write(OutputStream out) throws IOException {
        out.write(bytes, 0, bytes.length);
    }
    
}
