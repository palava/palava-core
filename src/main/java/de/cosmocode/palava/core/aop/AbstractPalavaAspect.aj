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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.inject.Binder;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Stage;

public abstract aspect AbstractPalavaAspect {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractPalavaAspect.class);
    
    private boolean alreadyInjected;
    
    pointcut createInjector(Stage stage, Module[] modules): call(Injector Guice.createInjector(Stage, Module...)) && args(stage, modules);
    
    Injector around(Stage stage, Module[] modules): createInjector(Stage, Module[]) && args(stage, modules) {
        return Guice.createInjector(stage, Lists.asList(new Module() {
                
                @Override
                public void configure(Binder binder) {
                    LOG.trace("Injecting members on {}", this);
                    Preconditions.checkState(!alreadyInjected, "Members have been already injected on {}", this);
                    binder.requestInjection(AbstractPalavaAspect.this);
                    alreadyInjected = true;
                }
                
        }, modules));
    }
    
    protected final void checkState() {
        Preconditions.checkState(alreadyInjected, "Members have not been injected on {}", this);
    }

}
