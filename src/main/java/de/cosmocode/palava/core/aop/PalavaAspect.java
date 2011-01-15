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

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.inject.Binder;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.Stage;
import com.google.inject.name.Named;

import de.cosmocode.commons.Throwables;
import de.cosmocode.palava.core.CoreConfig;

/**
 * Abstract palava {@link Aspect}.
 *
 * @since 2.9
 * @author Willi Schoenborn
 */
@Aspect("issingleton()")
public abstract class PalavaAspect {

    private static final Logger LOG = LoggerFactory.getLogger(PalavaAspect.class);    
    
    private boolean injected;
    private boolean reinjectable;

    private final Module module = new Module() {
        
        @Override
        public void configure(Binder binder) {
            checkInjectable();
            final PalavaAspect self = PalavaAspect.this;
            
            if (injected) {
                LOG.trace("Re-injecting members on {}", self);
            } else {
                LOG.trace("Injecting members on {}", self);
            }
            
            binder.requestInjection(self);
        }
        
    };
    
    private boolean isNotInjected() {
        return !injected;
    }
    
    private void checkInjectable() {
        Preconditions.checkState(isNotInjected() || reinjectable, "Illegal attempt to reinject members on %s", this);
    }
    
    /**
     * An injection point which marks this aspect as {@link #injected}.
     *
     * @since 2.9
     * @throws IllegalStateException if this aspect is already injected and does not allow reinjection
     */
    @Inject
    final void setInjected() {
        checkInjectable();
        this.injected = true;
    }
    
    /**
     * Optionally set {@link #reinjectable} of this aspect.
     *
     * @since 2.9
     * @param reinjectable true if this aspect allows reinjection, false otherwise
     * @throws IllegalStateException if this aspect is already injected and does not allow reinjection
     */
    @Inject(optional = true)
    final void setReinjectable(@Named(CoreConfig.REINJECTABLE_ASPECTS) boolean reinjectable) {
        this.reinjectable = reinjectable;
    }

    private Module[] append(Module[] modules) {
        final Module[] array = new Module[modules.length + 1];
        System.arraycopy(modules, 0, array, 0, modules.length);
        array[modules.length] = module;
        return array;
    }
    
    @Pointcut("call(com.google.inject.Injector com.google.inject.Guice.createInjector(" +
        "com.google.inject.Module...)) && args(modules)")
    @SuppressWarnings("unused")
    private void moduleArray(Module... modules) { }
    
    /**
     * An advice around {@link Guice#createInjector(Module...)} to inject members on this aspect.
     *
     * @since 2.9
     * @param point the proceeding joint point
     * @param modules the originally passed modules
     * @return the injector
     */
    @Around("moduleArray(modules)")
    public Object aroundModuleArray(ProceedingJoinPoint point, Module... modules) {
        try {
            return point.proceed(new Object[] {
                append(modules)
            });
        /* CHECKSTYLE:OFF */
        } catch (Throwable e) {
        /* CHECKSTYLE:ON */
            throw Throwables.sneakyThrow(e);
        }
    }

    @Pointcut("call(com.google.inject.Injector com.google.inject.Guice.createInjector(" +
        "java.lang.Iterable)) && args(modules)")
    @SuppressWarnings({ "unused", "unchecked" })
    private void moduleIterable(Iterable modules) { }

    /**
     * An advice around {@link Guice#createInjector(Iterable)} to inject members on this aspect.
     *
     * @since 2.9
     * @param point the proceeding joint point
     * @param modules the originally passed modules
     * @return the injector
     */
    @Around("moduleIterable(modules)")
    @SuppressWarnings("unchecked")
    public Object aroundModuleIterable(ProceedingJoinPoint point, Iterable modules) {
        try {
            return point.proceed(new Object[] {
                Iterables.concat(modules, Collections.singleton(module))
            });
        /* CHECKSTYLE:OFF */
        } catch (Throwable e) {
        /* CHECKSTYLE:ON */
            throw Throwables.sneakyThrow(e);
        }
    }

    @Pointcut("call(com.google.inject.Injector com.google.inject.Guice.createInjector(" +
        "com.google.inject.Stage, com.google.inject.Module...)) && args(stage, modules)")
    @SuppressWarnings("unused")
    private void stageAndModuleArray(Stage stage, Module... modules) { }
    
    /**
     * An advice around {@link Guice#createInjector(Stage, Module...)} to inject members on this aspect.
     *
     * @since 2.9
     * @param point the proceeding joint point
     * @param stage the originally passed stage
     * @param modules the originally passed modules
     * @return the injector
     */
    @Around("stageAndModuleArray(stage, modules)")
    public Object aroundStageAndModuleArray(ProceedingJoinPoint point, Stage stage, Module... modules) {
        try {
            return point.proceed(new Object[] {
                stage, append(modules)
            });
        /* CHECKSTYLE:OFF */
        } catch (Throwable e) {
        /* CHECKSTYLE:ON */
            throw Throwables.sneakyThrow(e);
        }
    }

    @Pointcut("call(com.google.inject.Injector com.google.inject.Guice.createInjector(" +
        "com.google.inject.Stage, java.lang.Iterable)) && args(stage, modules)")
    @SuppressWarnings({ "unused", "unchecked" })
    private void stageAndModuleIterable(Stage stage, Iterable modules) { }
    
    /**
     * An advice around {@link Guice#createInjector(Stage, Iterable)} to inject members on this aspect.
     *
     * @since 2.9
     * @param point the proceeding joint point
     * @param stage the originally passed stage
     * @param modules the originally passed stage
     * @return the injector
     */
    @Around("stageAndModuleIterable(stage, modules)")
    @SuppressWarnings("unchecked")
    public Object aroundStageAndModuleIterable(ProceedingJoinPoint point, Stage stage, 
            Iterable modules) {
        try {
            return point.proceed(new Object[] {
                stage, Iterables.concat(modules, Collections.singleton(module))
            });
        /* CHECKSTYLE:OFF */
        } catch (Throwable e) {
        /* CHECKSTYLE:ON */
            throw Throwables.sneakyThrow(e);
        }
    }
    
    /**
     * Checks that this aspect has been injected.
     *
     * @since 2.9
     * @throws IllegalStateException if members of this aspect have not yet been injected
     */
    protected final void checkState() {
        Preconditions.checkState(injected, "Members have not been injected on %s", this);
    }
    
}
