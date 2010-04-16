/**
 * palava - a java-php-bridge
 * Copyright (C) 2007-2010  CosmoCode GmbH
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package de.cosmocode.palava.core.lifecycle;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import com.google.inject.internal.Lists;
import com.google.inject.matcher.Matchers;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;


/**
 * Binds {@link InjectionListener} to initialize/start the corresponding lifecycle interfaces.
 *
 * @author Willi Schoenborn
 */
public final class LifecycleModule implements Module {

    private static final Logger LOG = LoggerFactory.getLogger(LifecycleModule.class);
    
    @Override
    public void configure(Binder binder) {
        final List<Object> services = Lists.newArrayList();
        binder.bind(new TypeLiteral<List<Object>>() { }).annotatedWith(LifecycleServices.class).toInstance(services);
        binder.bindListener(Matchers.any(), new TypeListener() {
            
            @Override
            public <I> void hear(TypeLiteral<I> literal, TypeEncounter<I> encounter) {
                if (Lifecycle.isInterface(literal.getRawType())) {
                    encounter.register(new InjectionListener<I>() {

                        @Override
                        public void afterInjection(I injectee) {
                            LOG.info("Bootstrapped service {}", injectee);
                            services.add(injectee);
                        };

                    });
                }
                encounter.register(new AutoStartableListener<I>());
                encounter.register(new InitializableListener<I>());
            }
            
        });
        binder.bind(DisposableListener.class).asEagerSingleton();
    }

}
