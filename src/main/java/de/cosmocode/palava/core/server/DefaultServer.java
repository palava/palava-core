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

package de.cosmocode.palava.core.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import de.cosmocode.palava.core.service.ServiceManager;
import de.cosmocode.palava.core.session.HttpSessionManager;

/**
 * Default implementation of the {@link Server} interface.
 *
 * @author Willi Schoenborn
 */
@Singleton
final class DefaultServer implements Server {

    private static final Logger log = LoggerFactory.getLogger(DefaultServer.class);

    private final ServiceManager serviceManager;
    
    private final HttpSessionManager sessionManager;
    
    @Inject
    public DefaultServer(ServiceManager serviceManager, HttpSessionManager sessionManager) {
        this.serviceManager = Preconditions.checkNotNull(serviceManager, "ServiceManager");
        this.sessionManager = Preconditions.checkNotNull(sessionManager, "SessionManager");
    }
    
    @Override
    public void run() {
        log.debug("ServiceManager: {}", serviceManager);
        log.debug("SessionManager: {}", sessionManager);
    }
    
}
