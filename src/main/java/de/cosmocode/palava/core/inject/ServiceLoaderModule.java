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

package de.cosmocode.palava.core.inject;

import java.util.ServiceLoader;

import com.google.inject.Binder;
import com.google.inject.Module;

/**
 * Once installed fetches all {@link Module}s configured via
 * the {@link ServiceLoader} configuration files and installs them.
 *
 * @since 2.3
 * @author Willi Schoenborn
 */
public class ServiceLoaderModule implements Module {

    private final Iterable<Module> modules;
    
    public ServiceLoaderModule() {
        this.modules = ServiceLoader.load(Module.class);
    }

    @Override
    public void configure(Binder binder) {
        for (Module module : modules) {
            binder.install(module);
        }
    }

}
