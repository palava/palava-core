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

package de.cosmocode.palava.core.session;

import java.text.Collator;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.internal.Maps;
import com.google.inject.servlet.SessionScoped;

import de.cosmocode.json.JSONRenderer;

/**
 * Default implementation of the {@link HttpSession} interface.
 *
 * @author Willi Schoenborn
 */
@SessionScoped
final class DefaultHttpSession implements HttpSession {
    
    public static final Logger log = Logger.getLogger(DefaultHttpSession.class);
    
    private static final String LANGUAGE_KEY = "lang";
    private static final String COUNTRY_KEY = "country";

    private String sessionId;

    private long accessTime;
    private boolean invalid;

    private final Map<Object, Object> data = Maps.newHashMap();
    
    private Locale locale;
    private NumberFormat format;
    private Collator collator;
    
    @Inject
    public DefaultHttpSession(@Assisted String sessionId) {
        this.sessionId = Preconditions.checkNotNull(sessionId, "SessionId");
    }

    @Override
    public Locale getLocale() {
        final Object langValue = data.get(LANGUAGE_KEY);
        if (locale == null || !locale.getLanguage().equals(langValue)) {
            
            format = null;
            collator = null;
            
            final Object countryValue = data.get(COUNTRY_KEY);
            
            if (langValue instanceof String && StringUtils.isNotBlank(String.class.cast(langValue))) {
                if (countryValue instanceof String && StringUtils.isNotBlank(String.class.cast(countryValue))) {
                    locale = new Locale(String.class.cast(langValue), String.class.cast(countryValue)); 
                } else {
                    locale = new Locale(String.class.cast(langValue));
                }
            } else {
                throw new IllegalStateException("No language found in session");
            }
        }
        return locale;
    }

    @Override
    public NumberFormat getNumberFormat() {
        if (format == null) {
            format = NumberFormat.getInstance(getLocale());
        }
        return format;
    }

    @Override
    public Collator getCollator() {
        if (collator == null) {
            collator = Collator.getInstance(getLocale());
        }
        return collator;
    }

    @Override
    public void destroy() {
        if (invalid) return;
        
        for (Destroyable destroyable : Iterables.filter(data.values(), Destroyable.class)) {
            destroyable.destroy(); 
        }
        
        data.clear();
        invalid = true;
    }

    @Override
    public void updateAccessTime() {
        accessTime = System.currentTimeMillis();
    }

    @Override
    public Date getAccessTime() {
        return new Date(accessTime);
    }

    @Override
    public String getSessionId() {
        return sessionId;
    }

    @Override
    public <K> boolean contains(K key) {
        return data.containsKey(key);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <K, V> V get(K key) {
        return (V) data.get(key);
    }

    @Override
    public <K, V> void putAll(Map<? extends K, ? extends V> map) {
        data.putAll(map);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <K, V> V remove(K key) {
        return (V) data.get(key);
    }

    @Override
    public <K, V> void set(K key, V value) {
        data.put(key, value);
    }

    @Override
    public JSONRenderer renderAsMap(JSONRenderer renderer) {
        return renderer.
            key("id").value(sessionId).
            key("accesstime").value(accessTime).
            key("data").object(data);
    }

}
