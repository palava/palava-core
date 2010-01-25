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

package de.cosmocode.palava.core.service;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matchers;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

/**
 * A {@link Module} for the {@link de.cosmocode.palava.core.service} package.
 *
 * @author Willi Schoenborn
 */
public final class ServiceModule implements Module {

    @Override
    public void configure(Binder binder) {
        binder.bind(ServiceManager.class).to(DefaultServiceManager.class);
        Multibinder.newSetBinder(binder, Service.class);
        
        binder.bindListener(Matchers.any(), new TypeListener() {
            
            @Override
            public <I> void hear(TypeLiteral<I> literal, TypeEncounter<I> encounter) {
                if (Service.class.isAssignableFrom(literal.getRawType())) {
                    encounter.register(new InitializableListener<I>());
                    encounter.register(new StartableListener<I>());
                }
            }
            
        });
    }

}
