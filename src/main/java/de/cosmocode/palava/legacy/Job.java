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

package de.cosmocode.palava.legacy;

import java.util.Map;

import de.cosmocode.palava.core.bridge.call.Call;
import de.cosmocode.palava.core.bridge.command.Command;
import de.cosmocode.palava.core.bridge.command.Response;
import de.cosmocode.palava.core.bridge.session.HttpSession;
import de.cosmocode.palava.legacy.server.Server;


/**
 * Legacy {@linkplain Command command}-style interface.
 * 
 * @deprecated <strong>Use {@link Command}!</strong>
 * 
 * @author Tobias Sarnowski
 * @author Oliver Lorenz 
 * @author Willi Schoenborn
 */
@Deprecated
public interface Job {

    /**
     * Executes this job.
     * 
     * @deprecated <strong>Use {@link Command}!</strong>
     * 
     * @param call the request of this job. contains the invoking args
     * @param response the container for the results 
     * @param session a session (may be null). available across different frontent/http requests
     * @param server the server structure - basically for the components lookup
     * @param caddy a container available across all jobs of the <strong>same</strong> frontend/http request
     * @throws Exception if execution failed
     */
    @Deprecated
    void process(Call call, Response response, HttpSession session, Server server, 
        Map<String, Object> caddy) throws Exception;

}
