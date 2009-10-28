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