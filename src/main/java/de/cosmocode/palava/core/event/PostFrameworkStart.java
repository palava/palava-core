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

package de.cosmocode.palava.core.event;

import de.cosmocode.collections.Procedure;
import de.cosmocode.palava.core.Registry;

/**
 * Clients being registered as {@link PostFrameworkStart} listeners
 * in the {@link Registry} will be notified after a successful framework start.
 *
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
