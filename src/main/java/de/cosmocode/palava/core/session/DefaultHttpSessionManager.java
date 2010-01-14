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

package de.cosmocode.palava.core.session;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.inject.Singleton;

/**
 * Manages all sessions.
 * 
 * @author Detlef Hüttemann
 * @author Willi Schoenborn
 */
@Singleton
final class DefaultHttpSessionManager implements HttpSessionManager {
    
    private static final Logger log = LoggerFactory.getLogger(DefaultHttpSessionManager.class);

    private final Map<String, HttpSession> sessions = new HashMap<String, HttpSession>();

    @Override
    public synchronized HttpSession get(String sessionId) {
        return sessions.get(sessionId);
    }

    @Override
    public void purge() {
        final Date date = new Date();
        final Collection<HttpSession> purged;
        
        synchronized (this) {
            purged = Collections2.filter(sessions.values(), new Predicate<HttpSession>() {
                
                @Override
                public boolean apply(HttpSession input) {
                    return input.getAccessTime().before(date);
                }
                
            });
            sessions.values().removeAll(purged);
        }
        
        for (HttpSession session : purged) {
            log.debug("purged {}", session.toString());
            session.destroy();
            // TODO session scope destroy
        }
    }

    @Override
    public HttpSession createSession() {
        final StringBuilder builder = new StringBuilder();
        final Random rnd = new Random();
        for (int n = 0; n < 64;  n++) {
            builder.append(rnd.nextInt(10));
        }
        final String sessionId = builder.toString();
        final HttpSession session = create(sessionId);
        // TODO session scope enter
        sessions.put(sessionId, session);
        return session;
    }
    
    @Override
    public HttpSession create(String sessionId) {
        return new DefaultHttpSession(sessionId);
    }

}
