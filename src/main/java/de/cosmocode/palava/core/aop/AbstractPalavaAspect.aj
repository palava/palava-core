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

package de.cosmocode.palava.core.aop;

import java.util.Collections;

import org.aspectj.lang.annotation.SuppressAjWarnings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.inject.Binder;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Stage;

/**
 * Abstract aspect which intercepts {@link Guice#createInjector(..)} calls
 * to get hands on the injector.
 *
 * @since 2.0
 * @author Willi Schoenborn
 */
public abstract aspect AbstractPalavaAspect {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractPalavaAspect.class);
    
    private final Module module = new Module() {
        
        @Override
        public void configure(Binder binder) {
            final AbstractPalavaAspect self = AbstractPalavaAspect.this;
            LOG.trace("Injecting members on {}", self);
            Preconditions.checkState(!alreadyInjected, "Members have been already injected on %s", self);
            binder.requestInjection(self);
            alreadyInjected = true;
        }
        
    };
    
    private boolean alreadyInjected;
    
    private Module[] append(Module[] modules) {
        final Module[] array = new Module[modules.length + 1];
        System.arraycopy(modules, 0, array, 0, modules.length);
        array[modules.length] = module;
        return array;
    }
    
    private Iterable<Module> append(Iterable<? extends Module> modules) {
        return Iterables.concat(modules, Collections.singleton(module));
    }
    
    @SuppressAjWarnings("adviceDidNotMatch")
    Injector around(Module[] modules): call(Injector Guice.createInjector(Module...)) && args(modules) {
        return proceed(append(modules));
    }

    @SuppressAjWarnings("adviceDidNotMatch")
    Injector around(Iterable<? extends Module> modules): 
        call(Injector Guice.createInjector(Iterable<? extends Module>)) && args(modules) {
        return proceed(append(modules));
    }

    @SuppressAjWarnings("adviceDidNotMatch")
    Injector around(Stage stage, Module[] modules): 
        call(Injector Guice.createInjector(Stage, Module...)) && args(stage, modules) {
        return proceed(stage, append(modules));
    }

    @SuppressAjWarnings("adviceDidNotMatch")
    Injector around(Stage stage, Iterable<? extends Module> modules): 
        call(Injector Guice.createInjector(Stage, Iterable<? extends Module>)) && args(stage, modules) {
        return proceed(stage, append(modules));
    }
    
    protected final void checkState() {
        Preconditions.checkState(alreadyInjected, "Members have not been injected on %s", this);
    }

}
