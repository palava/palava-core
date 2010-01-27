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

package de.project.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.cosmocode.palava.core.bridge.request.HttpRequest;
import de.cosmocode.palava.core.bridge.request.RequestFilter;

final class LogRequestFilter implements RequestFilter {

    private static final Logger log = LoggerFactory.getLogger(LogRequestFilter.class);

    @Override
    public void after(HttpRequest request) {
        log.debug("HttpRequest on {}, from {} ({})", new Object[] {
            request.getRequestUri(), request.getRemoteAddress(), request.getUserAgent()
        });
    }

    @Override
    public void before(HttpRequest request) {
        // TODO Auto-generated method stub

    }

}
