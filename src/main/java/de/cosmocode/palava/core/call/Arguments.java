package de.cosmocode.palava.core.call;

import de.cosmocode.collections.utility.UtilityMap;

/**
 * 
 *
 * @author Willi Schoenborn
 */
public interface Arguments extends UtilityMap<String, Object> {

    void require(String... keys) throws MissingArgumentException;
    
}
