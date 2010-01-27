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

package de.cosmocode.palava.core.bridge.session;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.inject.Singleton;

import de.cosmocode.palava.core.bridge.scope.Scopes;
import de.cosmocode.palava.core.framework.lifecycle.Disposable;

/**
 * Manages all sessions.
 * 
 * @author Detlef Hüttemann
 * @author Willi Schoenborn
 */
@Singleton
final class DefaultHttpSessionManager implements HttpSessionManager, Disposable {
    
    private static final Logger log = LoggerFactory.getLogger(DefaultHttpSessionManager.class);
    
    private final Map<String, HttpSession> sessions = new HashMap<String, HttpSession>();

    @Override
    public HttpSession get(String sessionId) {
        synchronized (sessions) {
            return sessions.get(sessionId);
        }
    }

    @Override
    public void destroyAll() {
        destroy(0, TimeUnit.MILLISECONDS);
    }
    
    @Override
    public void destroy(long period, TimeUnit periodUnit) {
        final long then = System.currentTimeMillis() - periodUnit.toMillis(period);
        final Date date = new Date(then);

        final Predicate<HttpSession> predicate = new Predicate<HttpSession>() {
            
            @Override
            public boolean apply(HttpSession input) {
                return input.getAccessTime().before(date);
            }
            
        };
        
        synchronized (sessions) {
            final Collection<HttpSession> oldSessions = Collections2.filter(sessions.values(), predicate);
            log.info("Destroying {} sessions", oldSessions.size());
            for (HttpSession session : oldSessions) {
                session.destroy();
            }
            sessions.values().removeAll(oldSessions);
        }
    }

    @Override
    public HttpSession get() {
        final HttpSession cached = Scopes.getCurrentSession();
        if (cached == null) {
            final StringBuilder builder = new StringBuilder();
            final Random rnd = new Random();
            for (int n = 0; n < 64;  n++) {
                builder.append(rnd.nextInt(10));
            }
            final String sessionId = builder.toString();
            log.debug("Creating new session with id {}", sessionId);
            final HttpSession session = new DefaultHttpSession(sessionId);
            
            synchronized (sessions) {
                sessions.put(sessionId, session);
            }
            
            log.debug("{} sessions currently in progess", sessions.size());
            
            return session;
        } else {
            log.debug("Found old session");
            return cached;
        }
    }
    
    @Override
    public void dispose() {
        destroyAll();
    }
    
}
