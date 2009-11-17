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

package de.cosmocode.palava.jobs;

import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import de.cosmocode.palava.MissingArgumentException;

public abstract class AbstractUtilityMap<K, V> implements Map<K, V>, UtilityMap<K, V> {

	private static final Pattern CHARACTER_PATTERN = Pattern.compile("[A-Za-z]");
	
	@Override
	public boolean getBoolean(K key) throws MissingArgumentException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean getBoolean(K key, boolean defaultValue) {
		throw new UnsupportedOperationException();
	}

	@Override
	public byte getByte(K key) throws MissingArgumentException {
		final Object value;
		if (!containsKey(key)) {
			throw new MissingArgumentException(key);
		} else if ((value = get(key)) == null) {
			throw new NumberFormatException(key + " is null");
		} else if (value instanceof Number) {
			return Number.class.cast(value).byteValue();
		} else if (value instanceof String) {
			return Byte.parseByte(String.class.cast(value));
		} else {
			return Byte.parseByte(value.toString());
		}
	}

	@Override
	public byte getByte(K key, byte defaultValue) {
		throw new UnsupportedOperationException();
	}

	@Override
	public short getShort(K key) throws MissingArgumentException {
		final Object value;
		if (!containsKey(key)) {
			throw new MissingArgumentException(key);
		} else if ((value = get(key)) == null) {
			throw new NumberFormatException(key + " is null");
		} else if (value instanceof Number) {
			return Number.class.cast(value).shortValue();
		} else if (value instanceof String) {
			return Short.parseShort(String.class.cast(value));
		} else {
			return Short.parseShort(value.toString());
		}
	}

	@Override
	public short getShort(K key, short defaultValue) {
		throw new UnsupportedOperationException();
	}

	@Override
	public char getChar(K key) throws MissingArgumentException {
		final Object value;
		if (!containsKey(key)) {
			throw new MissingArgumentException(key);
		}  else if ((value = get(key)) == null) {
			throw new NumberFormatException(key + " is null");
		} else {
			final String stringValue = value instanceof String ? String.class.cast(value) : value.toString();
			if (stringValue.length() != 1) throw new IllegalArgumentException("Cannot parse char out of " + stringValue);
			if (!StringUtils.isAlpha(stringValue)) throw new IllegalArgumentException("Value must be alpha numeric, but was '" + stringValue + "'");
			return stringValue.charAt(0);
		}
	}

	@Override
	public char getChar(K key, char defaultValue) {
		final Object value = get(key);
		if (value == null) return defaultValue;
		final String stringValue = value instanceof String ? String.class.cast(value) : value.toString();
		if (CHARACTER_PATTERN.matcher(stringValue).matches()) {
			return stringValue.charAt(0);
		}
		return defaultValue;
	}

	@Override
	public int getInt(K key) throws MissingArgumentException {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getInt(K key, int defaultValue) {
		throw new UnsupportedOperationException();
	}

	@Override
	public long getLong(K key) throws MissingArgumentException {
		throw new UnsupportedOperationException();
	}

	@Override
	public long getLong(K key, long defaultValue) {
		throw new UnsupportedOperationException();
	}

	@Override
	public float getFloat(K key) throws MissingArgumentException {
		final Object value;
		if (!containsKey(key)) {
			throw new MissingArgumentException(key);
		} else if ((value = get(key)) == null) {
			throw new NumberFormatException(key + " is null");
		} else if (value instanceof Number) {
			return Number.class.cast(value).floatValue();
		} else if (value instanceof String) {
			return Float.parseFloat(String.class.cast(value));
		} else {
			return Float.parseFloat(value.toString());
		}
	}

	@Override
	public float getFloat(K key, float defaultValue) {
		throw new UnsupportedOperationException();
	}

	@Override
	public double getDouble(K key) throws MissingArgumentException {
		throw new UnsupportedOperationException();
	}

	@Override
	public double getDouble(K key, double defaultValue) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getString(K key) throws MissingArgumentException {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getString(K key, String defaultValue) {
		throw new UnsupportedOperationException();
	}

}