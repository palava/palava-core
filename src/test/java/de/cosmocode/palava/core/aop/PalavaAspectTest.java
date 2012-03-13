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

import org.junit.Assert;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Stage;

/**
 * Tests {@link PalavaAspect}.
 *
 * @since 2.9
 * @author Willi Schoenborn
 */
public final class PalavaAspectTest {

    /**
     * Tests whether {@link TrueTarget#call()} returns {@link Boolean#FALSE}
     * when {@link Guice#createInjector(Module...)} is used.
     *
     * @since 2.9
     */
    @Test
    public void moduleArrayTrue() {
        final Injector injector = Guice.createInjector(new ReinjectableAspectModule());
        final TrueTarget target = injector.getInstance(TrueTarget.class);
        Assert.assertSame(Boolean.FALSE, target.call());
    }

    /**
     * Tests whether {@link TrueTarget#call()} returns {@link Boolean#FALSE}
     * when {@link Guice#createInjector(Stage, Module...)} is used.
     *
     * @since 2.9
     */
    @Test
    public void stageAndModuleArrayTrue() {
        final Injector injector = Guice.createInjector(Stage.DEVELOPMENT, new ReinjectableAspectModule());
        final TrueTarget target = injector.getInstance(TrueTarget.class);
        Assert.assertSame(Boolean.FALSE, target.call());
    }

    /**
     * Tests whether {@link TrueTarget#call()} returns {@link Boolean#FALSE}
     * when {@link Guice#createInjector(Iterable)} is used.
     *
     * @since 2.9
     */
    @Test
    public void moduleIterableTrue() {
        final Iterable<Module> modules = Collections.<Module>singleton(new ReinjectableAspectModule());
        final Injector injector = Guice.createInjector(modules);
        final TrueTarget target = injector.getInstance(TrueTarget.class);
        Assert.assertSame(Boolean.FALSE, target.call());
    }

    /**
     * Tests whether {@link TrueTarget#call()} returns {@link Boolean#FALSE}
     * when {@link Guice#createInjector(Stage, Iterable)} is used.
     *
     * @since 2.9
     */
    @Test
    public void stageAndModuleIterableTrue() {
        final Iterable<Module> modules = Collections.<Module>singleton(new ReinjectableAspectModule());
        final Injector injector = Guice.createInjector(Stage.DEVELOPMENT, modules);
        final TrueTarget target = injector.getInstance(TrueTarget.class);
        Assert.assertSame(Boolean.FALSE, target.call());
    }

    /**
     * Tests whether {@link FalseTarget#call()} returns {@link Boolean#TRUE}
     * when {@link Guice#createInjector(Module...)} is used.
     *
     * @since 2.9
     */
    @Test
    public void moduleArrayFalse() {
        final Injector injector = Guice.createInjector(new ReinjectableAspectModule());
        final FalseTarget target = injector.getInstance(FalseTarget.class);
        Assert.assertSame(Boolean.TRUE, target.call());
    }

    /**
     * Tests whether {@link FalseTarget#call()} returns {@link Boolean#TRUE}
     * when {@link Guice#createInjector(Stage, Module...)} is used.
     *
     * @since 2.9
     */
    @Test
    public void stageAndModuleArrayFalse() {
        final Injector injector = Guice.createInjector(Stage.DEVELOPMENT, new ReinjectableAspectModule());
        final FalseTarget target = injector.getInstance(FalseTarget.class);
        Assert.assertSame(Boolean.TRUE, target.call());
    }

    /**
     * Tests whether {@link FalseTarget#call()} returns {@link Boolean#TRUE}
     * when {@link Guice#createInjector(Iterable)} is used.
     *
     * @since 2.9
     */
    @Test
    public void moduleIterableFalse() {
        final Iterable<Module> modules = Collections.<Module>singleton(new ReinjectableAspectModule());
        final Injector injector = Guice.createInjector(modules);
        final FalseTarget target = injector.getInstance(FalseTarget.class);
        Assert.assertSame(Boolean.TRUE, target.call());
    }

    /**
     * Tests whether {@link FalseTarget#call()} returns {@link Boolean#TRUE}
     * when {@link Guice#createInjector(Stage, Iterable)} is used.
     *
     * @since 2.9
     */
    @Test
    public void stageAndModuleIterableFalse() {
        final Iterable<Module> modules = Collections.<Module>singleton(new ReinjectableAspectModule());
        final Injector injector = Guice.createInjector(Stage.DEVELOPMENT, modules);
        final FalseTarget target = injector.getInstance(FalseTarget.class);
        Assert.assertSame(Boolean.TRUE, target.call());
    }
    
}
