/**
 * palava - a java-php-bridge
 * Copyright (C) 2007  CosmoCode GmbH
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

package de.cosmocode.palava.core.call;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Key;
import com.google.inject.internal.Maps;

import de.cosmocode.palava.core.command.filter.Filter;
import de.cosmocode.palava.core.command.filter.FilterChain;
import de.cosmocode.palava.core.command.filter.FilterException;
import de.cosmocode.palava.core.protocol.Call;
import de.cosmocode.palava.core.protocol.Response;

/**
 * A {@link Filter} which enters and exits the {@link RequestScope}.
 *
 * @author Willi Schoenborn
 */
final class CallScopedFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(CallScopedFilter.class);

    private boolean entered;
    private final Map<Key<?>, Object> context = Maps.newHashMap(); 
    
    @Override
    public void filter(Call request, Response response, FilterChain chain) throws FilterException {
        log.debug("Entering call scope");
        entered = true;
        // TODO enter scope
        
        chain.filter(request, response);
        
        log.debug("Exiting call scope");
        entered = false;
        context.clear();
        // TODO exit scope
    }

}
