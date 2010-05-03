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

package de.cosmocode.palava.core.event;

import de.cosmocode.collections.Procedure;
import de.cosmocode.palava.core.Registry;

/**
 * Clients being registered as {@link PostFrameworkStart} listeners
 * in the {@link Registry} will be notified after a successful framework start.
 *
 * @since 2.0
 * @author Tobias Sarnowski
 * @author Willi Schoenborn
 */
public interface PostFrameworkStart {
    
    Procedure<PostFrameworkStart> PROCEDURE = new Procedure<PostFrameworkStart>() {
        
        @Override
        public void apply(PostFrameworkStart input) {
            input.eventPostFrameworkStart();
        }
        
    };
    
    /**
     * Post framework start callback. 
     */
    void eventPostFrameworkStart();
    
}
