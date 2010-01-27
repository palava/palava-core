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

package de.cosmocode.palava.bridge.scope;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import de.cosmocode.palava.bridge.call.Call;
import de.cosmocode.palava.bridge.call.filter.Filter;
import de.cosmocode.palava.bridge.call.filter.FilterChain;
import de.cosmocode.palava.bridge.call.filter.FilterException;
import de.cosmocode.palava.bridge.simple.content.Content;

/**
 * A {@link Filter} which handles {@link CallScope} entering/exiting.
 *
 * @author Willi Schoenborn
 */
@Singleton
final class CallScopeFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(CallScopeFilter.class);

    private final CallScope scope;
    
    @Inject
    public CallScopeFilter(CallScope scope) {
        this.scope = Preconditions.checkNotNull(scope, "Scope");
    }
    
    @Override
    public Content filter(Call call, FilterChain chain) throws FilterException {
        log.debug("Entering call scope");
        Scopes.setCurrentCall(call);
        scope.enter();
        scope.seed(Call.class, call);

        try {
            return chain.filter(call);
        } finally {
            log.debug("Exiting call scope");
            scope.exit();
            Scopes.setCurrentCall(null);
        }
    }

}
