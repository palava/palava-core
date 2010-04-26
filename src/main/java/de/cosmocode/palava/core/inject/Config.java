package de.cosmocode.palava.core.inject;

import com.google.common.base.Preconditions;

/**
 * Reusable simple implementation of the Config interface.
 *
 * @author Willi Schoenborn
 */
public final class Config {

    private final String prefix;

    public Config(String prefix) {
        Preconditions.checkNotNull(prefix, "Prefix");
        this.prefix = prefix + ".";
    }

    /**
     * Returns a prefixed version of the given key.
     * 
     * @param key the key being prefixed
     * @return a prefixed key
     */
    public String prefixed(String key) {
        return prefix + key;
    }

}
