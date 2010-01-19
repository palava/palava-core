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

import com.google.inject.Provider;

/**
 * A manager for {@link HttpSession}s.
 *
 * @author Willi Schoenborn
 */
public interface HttpSessionManager extends Provider<HttpSession> {

    /**
     * Purge all sessions.
     */
    void purge();
    
    /**
     * Retrieve an {@link HttpSession} for a given session id.
     * 
     * @param sessionId the session's id
     * @return the found {@link HttpSession} or null if there was no session
     *         with the given id
     */
    HttpSession get(String sessionId);
    
}
