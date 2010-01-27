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

package de.cosmocode.palava.bridge.request;

import java.util.Map;

import de.cosmocode.palava.bridge.session.HttpSession;

/**
 * A factory for {@link HttpRequest}s.
 *
 * @author Willi Schoenborn
 */
public interface HttpRequestFactory {

    /**
     * Creates a new {@link HttpRequest} which will be associated with the specified
     * session and backed by the given server variable.
     * 
     * @param session the associated session
     * @param serverVariable the content of the $_SERVER variable
     * @return a new {@link HttpRequest}
     */
    HttpRequest create(HttpSession session, Map<String, String> serverVariable);
    
}
