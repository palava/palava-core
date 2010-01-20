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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

import de.cosmocode.palava.core.call.Call;
import de.cosmocode.palava.core.protocol.content.Content;

/**
 * Default implementation of the {@link FilterChain} interface.
 *
 * @author Willi Schoenborn
 */
final class DefaultFilterChain implements FilterChain {

    private static final Logger log = LoggerFactory.getLogger(DefaultFilterChain.class);

    private final List<Filter> filters;
    
    private final FilterChain proceedingChain;
    
    private int index = -1;
    
    public DefaultFilterChain(List<Filter> filters, FilterChain proceedingChain) {
        this.filters = Preconditions.checkNotNull(filters, "Filter");
        this.proceedingChain = Preconditions.checkNotNull(proceedingChain, "ProceedingChain");
    }

    @Override
    public Content filter(Call call) throws FilterException {
        index++;
        if (index < filters.size()) {
            final Filter filter = filters.get(index);
            log.debug("Running filter {}", filter);
            return filter.filter(call, this);
        } else {
            return proceedingChain.filter(call);
        }
    }

}
