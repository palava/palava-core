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

package de.cosmocode.palava.core.service.lifecycle;

import java.lang.annotation.Annotation;

import com.google.common.base.Preconditions;
import com.google.inject.Module;

/**
 * Static utility class for generating {@link Config} annotations during
 * runtime.
 *
 * @author Willi Schoenborn
 */
public final class Configs {
    
    private Configs() {
        
    }

    /**
     * Creates a new {@link Config} for the given class literal.
     * 
     * @param value the class literal for the config
     * @return a new {@link Config} annotation
     * @throws NullPointerException if value is null
     */
    public static Config of(final Class<? extends Module> value) {
        Preconditions.checkNotNull(value, "Value");
        return new Config() {
            
            @Override
            public Class<? extends Module> value() {
                return value;
            }
            
            @Override
            public Class<? extends Annotation> annotationType() {
                return Config.class;
            }
            
            @Override
            public int hashCode() {
                return (127 * "value".hashCode()) ^ value.hashCode();
            }
            
            @Override
            public boolean equals(Object that) {
                return that instanceof Config && value().equals(Config.class.cast(that).value());
            }
            
            @Override
            public String toString() {
                return "@" + Config.class.getName() + "(value=" + value + ")";
            }
            
        };
    }
    
}
