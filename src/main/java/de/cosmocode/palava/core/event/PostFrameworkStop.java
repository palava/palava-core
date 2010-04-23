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

/**
 * Event interface for post framework stop. This event will occur
 * right after {@link FrameworkStop}.
 * 
 * @since 2.4
 * @author Willi Schoenborn
 */
public interface PostFrameworkStop {

    Procedure<PostFrameworkStop> PROCEDURE = new Procedure<PostFrameworkStop>() {
        
        @Override
        public void apply(PostFrameworkStop input) {
            input.eventPostFrameworkStop();
        }
        
    };
    
    /**
     * Event callback.
     */
    void eventPostFrameworkStop();
    
}
