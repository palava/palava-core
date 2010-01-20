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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.inject.Injector;

import de.cosmocode.palava.core.call.Call;
import de.cosmocode.palava.core.call.filter.definition.FilterDefinition;
import de.cosmocode.palava.core.protocol.content.Content;
import de.cosmocode.patterns.Adapter;

/**
 * Adapts a {@link FilterDefinition} to the {@link Filter} interface. 
 *
 * @author Willi Schoenborn
 */
@Adapter(Filter.class)
final class FilterDefinitionAdapter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(FilterDefinitionAdapter.class);

    private final Filter filter;
    
    private final FilterDefinition definition;

    public FilterDefinitionAdapter(Injector injector, FilterDefinition definition) {
        Preconditions.checkNotNull(injector, "Injector");
        this.filter = injector.getInstance(definition.getKey());
        this.definition = Preconditions.checkNotNull(definition, "Definition");
    }

    @Override
    public Content filter(Call call, FilterChain chain) throws FilterException {
        if (definition.appliesTo(call.getCommand())) {
            log.debug("Filtering {} using {}", call, filter);
            return filter.filter(call, chain);
        } else {
            log.debug("Skipping filter executing of {} for {}", filter, call);
            return chain.filter(call);
        }
    }

    @Override
    public String toString() {
        return filter.toString();
    }
    
}
