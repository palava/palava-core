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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.spi.InjectionListener;



/**
 * {@link InjectionListener} which handles {@link Initializable}s.
 *
 * @since 2.3
 * @author Willi Schoenborn
 * @param <I> generic injectee type
 */
final class InitializableListener<I> implements InjectionListener<I> {
    
    private static final Logger LOG = LoggerFactory.getLogger(InitializableListener.class);

    @Override
    public void afterInjection(I injectee) {
        if (injectee instanceof Initializable) {
            LOG.info("Initializing service {}", injectee);
            Initializable.class.cast(injectee).initialize();
        }
    }
    
}
