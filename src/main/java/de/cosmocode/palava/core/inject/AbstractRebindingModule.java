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

import com.google.inject.Module;
import com.google.inject.PrivateModule;

/**
 * An abstract {@link PrivateModule} which forces implementors to specify every single
 * step when writing re-binding modules. This should reduce errors.
 *
 * @author Willi Schoenborn
 */
public abstract class AbstractRebindingModule extends PrivateModule implements RebindModule {

    private boolean overridden;
    
    @Override
    protected final void configure() {
        configuration();
        if (overridden) optionals();
        bindings();
        expose();
    }
    
    /**
     * Rebinds configuration entries.
     */
    protected abstract void configuration();
    
    /**
     * Rebinds optional configuration entries.
     */
    protected abstract void optionals();
    
    /**
     * Configures bindings.
     */
    protected abstract void bindings();
    
    /**
     * Expose specific bindings.
     */
    protected abstract void expose();

    @Override
    public final Module overrideOptionals() {
        overridden = true;
        return this;
    }
    
}
