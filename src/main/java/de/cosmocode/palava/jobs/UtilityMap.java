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

import de.cosmocode.palava.MissingArgumentException;

public interface UtilityMap<K, V> extends Map<K, V> {

    boolean getBoolean(K key) throws MissingArgumentException;
    
    boolean getBoolean(K key, boolean defaultValue);
    
    byte getByte(K key) throws MissingArgumentException;
    
    byte getByte(K key, byte defaultValue);
    
    short getShort(K key) throws MissingArgumentException;
    
    short getShort(K key, short defaultValue);
    
    char getChar(K key) throws MissingArgumentException;
    
    char getChar(K key, char defaultValue);
    
    int getInt(K key) throws MissingArgumentException;
    
    int getInt(K key, int defaultValue);
    
    long getLong(K key) throws MissingArgumentException;
    
    long getLong(K key, long defaultValue);
    
    float getFloat(K key) throws MissingArgumentException;
    
    float getFloat(K key, float defaultValue);
    
    double getDouble(K key) throws MissingArgumentException;
    
    double getDouble(K key, double defaultValue);
    
    String getString(K key) throws MissingArgumentException;
    
    String getString(K key, String defaultValue);
    
}