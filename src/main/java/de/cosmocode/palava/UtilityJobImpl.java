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

import java.util.Arrays;

import org.apache.commons.lang.StringUtils;


public abstract class UtilityJobImpl implements Job, UtilityJob {
    
    @Override
    public void require(String... keys) throws MissingArgumentException {
        for (String key : keys) {
            if (!hasArgument(key)) throw new MissingArgumentException(key);
        }
    }
    
    @Override
    public void requireOneOf(String... keys) throws MissingArgumentException,
            Exception {
        boolean anyArgumentGiven = false;
        for (String key : keys) {
            if (hasArgument(key)) anyArgumentGiven = true;
        }
        if (!anyArgumentGiven) throw new MissingArgumentException("need one of " + Arrays.toString(keys));
    }

    @Override
    public boolean getBool(String key) throws MissingArgumentException {
        if (hasArgument(key))
            return getOptBool(key);
        else throw new MissingArgumentException(this, key, "boolean");
    }
    
    @Override
    public boolean getOptBool(String key) {
        String param = getOptional(key);
        return StringUtils.isNotBlank(param) && !param.equalsIgnoreCase("false");
    }
    
    @Override
    public boolean getOptBool(String key, boolean defaultValue) {
        if (hasArgument(key)) {
            return getOptBool(key);
        } else {
            return defaultValue;
        }
    }
    

    
    @Override
    public int getInt(String key) throws MissingArgumentException {
        if (hasArgument(key))
            return Integer.parseInt(getOptional(key));
        else throw new MissingArgumentException(this, key, "int");
    }
    
    @Override
    public int getOptInt(String key) {
        return getOptInt(key, 0);
    }
    
    @Override
    public int getOptInt(String key, int defaultValue) {
        if (hasArgument(key)) {
            final String val = getOptional(key);
            if (StringUtils.isNumeric(val)) {
                return Integer.parseInt(val);
            }
        }
        return defaultValue;
    }
    
    
    
    @Override
    public long getLong(String key) throws MissingArgumentException {
        if (hasArgument(key))
            return Integer.parseInt(getOptional(key));
        else throw new MissingArgumentException(this, key, "int");
    }
    
    @Override
    public long getOptLong(String key) {
        return getOptLong(key, 0);
    }
    
    @Override
    public long getOptLong(String key, long defaultValue) {
        if (hasArgument(key)) {
            final String val = getOptional(key);
            if (StringUtils.isNumeric(val)) {
                return Long.parseLong(val);
            }
        }
        return defaultValue;
    }
    
    public double optDouble(String key, double defaultValue) {
        if (hasArgument(key)) {
            final String val = getOptional(key);
            try {
                return Double.parseDouble(val);
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        }
        return defaultValue;
    }
    
    
    // -----------------
    // alternative names
    
    public boolean getOptionalBool(String key) {
        return getOptBool(key);
    }
    public boolean getOptionalBool(String key, boolean defaultValue) {
        return getOptBool(key, defaultValue);
    }
    public int getOptionalInt(String key) {
        return getOptInt(key);
    }
    public int getOptionalInt(String key, int defaultValue) {
        return getOptInt(key, defaultValue);
    }
    public long getOptionalLong(String key) {
        return getOptLong(key);
    }
    public long getOptionalLong(String key, long defaultValue) {
        return getOptLong(key, defaultValue);
    }
    
    
    // -----------------
    // deprecated
    // -----------------
    
    @Deprecated
    public String lookup (String key) throws MissingArgumentException, Exception {
        return getMandatory(key);
    }
    @Deprecated
    public String lookup (String key, String argumentType) throws MissingArgumentException, Exception {
        return getMandatory(key, argumentType);
    }
    @Deprecated
    public String lookupOptional (String key) {
        return getOptional(key);
    }
    @Deprecated
    public String lookupOptional (String key, String defaultValue) {
        return getOptional(key, defaultValue);
    }
    @Deprecated
    public boolean lookupBool (String key) throws MissingArgumentException {
        return getBool(key);
    }
    @Deprecated
    public boolean lookupOptionalBool (String key) {
        return getOptBool(key);
    }
    @Deprecated
    public int lookupInt (String key) throws MissingArgumentException {
        return getInt(key);
    }
    @Deprecated
    public int lookupOptionalInt (String key) {
        return getOptInt(key);
    }
    @Deprecated
    public int lookupOptionalInt (String key, int defaultValue) {
        return getOptInt(key, defaultValue);
    }

}
