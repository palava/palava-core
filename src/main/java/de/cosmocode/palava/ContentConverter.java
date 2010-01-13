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

package de.cosmocode.palava;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * abstract, implements the convert() function.
 * 
 * @author Detlef Hüttemann
 * @deprecated use JSONRenderer instead
 */
@Deprecated
public abstract class ContentConverter {

    public void convert(StringBuffer buf, Object object) throws ConversionException {
        if (object == null) {
            convertNull(buf);
        } else if (object instanceof String) {
            convertString(buf, (String) object);
        } else if (object instanceof java.util.Date) {
            convertDate(buf, (java.util.Date) object);
        } else if (object instanceof Number) {
            convertNumber(buf, (Number) object);
        } else if (object instanceof List<?>) {
            convertList(buf, List.class.cast(object));
        } else if (object instanceof Set<?>) {
            convertList(buf, new ArrayList<Object>((Set<?>) object));
        } else if (object instanceof Map<?, ?>) {
            convertMap(buf, Map.class.cast(object));
        } else if (object instanceof Convertible) {
            ((Convertible) object).convert(buf, this);
        } else {
            convertDefault(buf, object);
        }
    }

    public abstract void convertNull(StringBuffer buf) throws ConversionException;
    
    public abstract void convertString(StringBuffer buf, String object) throws ConversionException;
    
    public abstract void convertDate(StringBuffer buf, java.util.Date object) throws ConversionException;
    
    public abstract void convertNumber(StringBuffer buf, Number object) throws ConversionException;
    
    public abstract void convertList(StringBuffer buf, List<?> object) throws ConversionException;
    
    public abstract void convertMap(StringBuffer buf, Map<?, ?> object) throws ConversionException;
    
    public abstract void convertDefault(StringBuffer buf, Object object) throws ConversionException;
    
    public abstract void convertKeyValue(StringBuffer buf, String key, Object value, KeyValueState state) throws ConversionException;

}
