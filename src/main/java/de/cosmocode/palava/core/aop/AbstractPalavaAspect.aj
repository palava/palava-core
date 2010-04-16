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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
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

public abstract aspect AbstractPalavaAspect {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractPalavaAspect.class);
    
    private final Module module = new Module() {
        
        @Override
        public void configure(Binder binder) {
            final AbstractPalavaAspect self = AbstractPalavaAspect.this;
            LOG.trace("Injecting members on {}", self);
            Preconditions.checkState(!alreadyInjected, "Members have been already injected on {}", self);
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
    Injector around(Iterable<? extends Module> modules): call(Injector Guice.createInjector(Iterable<? extends Module>)) && args(modules) {
        return proceed(append(modules));
    }

    @SuppressAjWarnings("adviceDidNotMatch")
    Injector around(Stage stage, Module[] modules): call(Injector Guice.createInjector(Stage, Module...)) && args(stage, modules) {
        return proceed(stage, append(modules));
    }

    @SuppressAjWarnings("adviceDidNotMatch")
    Injector around(Stage stage, Iterable<? extends Module> modules): call(Injector Guice.createInjector(Stage, Iterable<? extends Module>)) && args(stage, modules) {
        return proceed(stage, append(modules));
    }
    
    protected final void checkState() {
        Preconditions.checkState(alreadyInjected, "Members have not been injected on {}", this);
    }

}
