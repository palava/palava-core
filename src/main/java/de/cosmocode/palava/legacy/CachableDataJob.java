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
import de.cosmocode.palava.core.bridge.call.DataCall;
import de.cosmocode.palava.core.bridge.call.MissingArgumentException;
import de.cosmocode.palava.core.bridge.command.Response;
import de.cosmocode.palava.core.bridge.session.HttpSession;
import de.cosmocode.palava.core.bridge.simple.ConnectionLostException;
import de.cosmocode.palava.legacy.server.Server;

/**
 * @deprecated use Cache annotation
 *
 * @author Willi Schoenborn
 */
@Deprecated
public abstract class CachableDataJob extends CachableJob {
    
    private Map<String, String> args;

    @Override
    @SuppressWarnings("unchecked")
    public final void process(Call request, Response response, Server server, HttpSession session, 
        Map<String, Object> caddy) throws ConnectionLostException, Exception {
        
        DataCall dataRequest = (DataCall) request;
        args = dataRequest.getStringedArguments();
        
        process(args, response, session, server, caddy);
    }
    
    protected abstract void process(Map<String, String> args, Response response, HttpSession session, Server server, 
        Map<String, Object> caddy) throws ConnectionLostException, Exception;
    
    
    
    // methods implemented from UtilityJob

    @Override
    public String getMandatory(String key) throws MissingArgumentException {
        if (args.containsKey(key)) return args.get(key);
        else throw new MissingArgumentException(this, key);
    }

    @Override
    public String getMandatory(String key, String argumentType) throws MissingArgumentException {
        if (args.containsKey(key)) return args.get(key);
        else throw new MissingArgumentException(this, key, argumentType);
    }

    @Override
    public String getOptional(String key) {
        return args.get(key);
    }

    @Override
    public String getOptional(String key, String defaultValue) {
        if (args.containsKey(key)) return args.get(key);
        else return defaultValue;
    }

    @Override
    public boolean hasArgument(String key) {
        return args.containsKey(key);
    }
    
}