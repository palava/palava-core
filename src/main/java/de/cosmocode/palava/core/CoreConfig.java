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

package de.cosmocode.palava.core;

/**
 * Constant holder class for configuration keys.
 * 
 * @since 2.0
 * @author Tobias Sarnowski
 * @author Willi Schoenborn
 */
public final class CoreConfig {

    public static final String PREFIX = "core.";

    public static final String APPLICATION = PREFIX + "application";
    
    public static final String STAGE = PREFIX + "stage";
    
    public static final String REINJECTABLE_ASPECTS = PREFIX + "reinjectableAspects";
    
    private CoreConfig() {
        
    }
}
