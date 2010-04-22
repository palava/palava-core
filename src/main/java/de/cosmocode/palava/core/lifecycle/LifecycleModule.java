/**
 * Copyright 2010 CosmoCode GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
