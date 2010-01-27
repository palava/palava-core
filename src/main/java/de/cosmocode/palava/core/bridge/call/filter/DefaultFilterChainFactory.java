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

package de.cosmocode.palava.core.bridge.call.filter;

import java.util.List;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.inject.Binding;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;

import de.cosmocode.palava.core.bridge.call.filter.definition.FilterDefinition;

/**
 * Default implementation of the {@link FilterChainFactory} interface.
 *
 * @author Willi Schoenborn
 */
@Singleton
final class DefaultFilterChainFactory implements FilterChainFactory {

    private final ImmutableList<Filter> filters;
    
    @Inject
    public DefaultFilterChainFactory(final Injector injector) {
        Preconditions.checkNotNull(injector, "Injector");
        
        final ImmutableList.Builder<FilterDefinition> builder = ImmutableList.builder();

        for (Binding<?> entry : injector.findBindingsByType(new TypeLiteral<List<FilterDefinition>>() { })) {

            // guarded by findBindingsByType()
            @SuppressWarnings("unchecked")
            final Key<List<FilterDefinition>> key = (Key<List<FilterDefinition>>) entry.getKey();
            builder.addAll(injector.getInstance(key));
        }
        
        final List<Filter> transformed = Lists.transform(builder.build(), new Function<FilterDefinition, Filter>() {
            
            @Override
            public Filter apply(FilterDefinition from) {
                return new FilterDefinitionAdapter(injector, from);
            }
            
        });
        
        this.filters = ImmutableList.copyOf(transformed);
    }

    @Override
    public FilterChain create(FilterChain proceeding) {
        return new DefaultFilterChain(filters, proceeding);
    }

}
