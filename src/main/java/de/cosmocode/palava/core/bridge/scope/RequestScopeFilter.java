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

package de.cosmocode.palava.core.bridge.scope;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Singleton;

import de.cosmocode.palava.core.bridge.request.HttpRequest;
import de.cosmocode.palava.core.bridge.request.RequestFilter;

/**
 * A {@link RequestFilter} which handles {@link RequestScope} entering/exiting.
 *
 * @author Willi Schoenborn
 */
@Singleton
final class RequestScopeFilter implements RequestFilter {

    private static final Logger log = LoggerFactory.getLogger(RequestScopeFilter.class);

    @Override
    public void before(HttpRequest request) {
        log.debug("Entering request scope");
        Scopes.setCurrentRequest(request);
    }

    @Override
    public void after(HttpRequest request) {
        log.debug("Exiting request scope");
        Scopes.setCurrentRequest(null);
    }

}
