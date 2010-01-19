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

package de.cosmocode.palava.core.call.filter;

import java.util.List;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;

import de.cosmocode.palava.core.call.Call;
import de.cosmocode.palava.core.call.filter.definition.FilterDefinition;
import de.cosmocode.palava.core.command.CommandException;
import de.cosmocode.palava.core.protocol.content.Content;

/**
 * Default implementation of the {@link FilterManager} interface.
 *
 * @author Willi Schoenborn
 */
@Singleton
final class DefaultFilterManager implements FilterManager {
    
    private final ImmutableList<Filter> filters;
    
    private final FilterChain chain;
    
    @Inject
    public DefaultFilterManager(final Injector injector, List<FilterDefinition> definitions) {
        Preconditions.checkNotNull(injector, "Injector"); 
        Preconditions.checkNotNull(definitions, "Definitions");
        
        this.filters = ImmutableList.copyOf(Lists.transform(definitions, new Function<FilterDefinition, Filter>() {
            
            @Override
            public Filter apply(FilterDefinition definition) {
                return new FilterDefinitionAdapter(injector, definition);
            }
            
        }));
        
        this.chain = new DefaultFilterChain(filters);
    }

    @Override
    public Content filter(Call call) throws FilterException, CommandException {
        // TODO 
        return chain.filter(call);
    }
    
}
