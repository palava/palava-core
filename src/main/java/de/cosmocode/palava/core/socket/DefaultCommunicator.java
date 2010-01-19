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

package de.cosmocode.palava.core.socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Functions;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import de.cosmocode.json.JSON;
import de.cosmocode.palava.core.call.Call;
import de.cosmocode.palava.core.call.filter.FilterChain;
import de.cosmocode.palava.core.call.filter.FilterChainFactory;
import de.cosmocode.palava.core.call.filter.FilterException;
import de.cosmocode.palava.core.command.Command;
import de.cosmocode.palava.core.command.CommandException;
import de.cosmocode.palava.core.command.CommandManager;
import de.cosmocode.palava.core.protocol.CallType;
import de.cosmocode.palava.core.protocol.ConnectionLostException;
import de.cosmocode.palava.core.protocol.Header;
import de.cosmocode.palava.core.protocol.JsonCall;
import de.cosmocode.palava.core.protocol.ProtocolAlgorithm;
import de.cosmocode.palava.core.protocol.ProtocolException;
import de.cosmocode.palava.core.protocol.content.Content;
import de.cosmocode.palava.core.protocol.content.ErrorContent;
import de.cosmocode.palava.core.protocol.content.JsonContent;
import de.cosmocode.palava.core.request.HttpRequest;
import de.cosmocode.palava.core.request.HttpRequestFactory;
import de.cosmocode.palava.core.request.HttpRequestFilter;
import de.cosmocode.palava.core.scope.Scopes;
import de.cosmocode.palava.core.session.HttpSession;
import de.cosmocode.palava.core.session.HttpSessionManager;

/**
 * Default implementation of the {@link Communicator} interface.
 *
 * @author Oliver Lorenz
 * @author Willi Schoenborn
 * @author Tilo Baller
 */
@Singleton
final class DefaultCommunicator implements Communicator {

    private static final Logger log = LoggerFactory.getLogger(DefaultCommunicator.class);
    
    private final ProtocolAlgorithm algorithm;
    
    private final HttpSessionManager sessionManager;
    
    private final HttpRequestFactory requestFactory;
    
    private final CommandManager commandManager;
    
    private final Set<HttpRequestFilter> requestFilters;
    
    private final FilterChainFactory chainFactory;
    
    @Inject
    public DefaultCommunicator(ProtocolAlgorithm algorithm, HttpSessionManager sessionManager,
        HttpRequestFactory requestFactory, CommandManager commandManager, Set<HttpRequestFilter> requestFilters,
        FilterChainFactory chainFactory) {
        this.algorithm = Preconditions.checkNotNull(algorithm, "Algorithm");
        this.sessionManager = Preconditions.checkNotNull(sessionManager, "SessionManager");
        this.requestFactory = Preconditions.checkNotNull(requestFactory, "RequestFactory");
        this.commandManager = Preconditions.checkNotNull(commandManager, "CommandManager");
        this.requestFilters = Preconditions.checkNotNull(requestFilters, "RequestFilters");
        this.chainFactory = Preconditions.checkNotNull(chainFactory, "ChainFactory");
    }
    
    private HttpRequest parse(InputStream input, OutputStream output) {
        final Header header = algorithm.read(input);
        if (header.getCallType() == CallType.OPEN) {
            final String sessionId = header.getSessionId();
            final HttpSession session = sessionManager.get(sessionId);
            if (StringUtils.isNotBlank(sessionId)) {
                if (session != null) {
                    session.updateAccessTime();
                }
            }
            final Call call = header.getCallType().createCall(header, input);
            final JsonCall jsonCall = JsonCall.class.cast(call);
            
            final JSONObject object;
            
            try {
                object = jsonCall.getJSONObject();
                call.discard();
                JsonContent.EMPTY.write(output);
            } catch (JSONException e) {
                throw new ProtocolException(e);
            } catch (IOException e) {
                throw new ProtocolException(e);
            }
            
            final Map<String, String> serverVariable = Maps.transformValues(
                JSON.asMap(object), Functions.toStringFunction()
            );
            final HttpRequest request = requestFactory.create(session, serverVariable);
            return request;
        } else {
            throw new ProtocolException("First call must be of type OPEN");
        }
    }
    
    private void before(HttpRequest request) {
        for (HttpRequestFilter filter : requestFilters) {
            filter.before(request);
        }
    }
    
    @Override
    public void communicate(InputStream input, OutputStream output) {
        final HttpRequest request = parse(input, output);
        before(request);
        while (true) {
            final Header header = algorithm.read(input);
            if (header.getCallType() == CallType.CLOSE) break;
            
            final Call call = header.getCallType().createCall(header, input);
            final Content content = process(call);
            
            try {
                call.discard();
                algorithm.sendTo(content, output);
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }
        after(request);
    }
    
    private Content process(Call call) {
        final String aliasedName = call.getHeader().getAliasedName();
        
        final Command command;
        
        /*CHECKSTYLE:OFF*/
        try {
            command = commandManager.forName(aliasedName);
        } catch (RuntimeException e) {
            return ErrorContent.create(e);
        }
        /*CHECKSTYLE:ON*/
        
        return filterAndExecute(command, call);
    }
    
    private Content filterAndExecute(final Command command, Call call) {
        /*CHECKSTYLE:OFF*/
        try {
            return chainFactory.create(new FilterChain() {
                
                @Override
                public Content filter(Call call) throws FilterException {
                    try {
                        return command.execute(call);
                    } catch (CommandException e) {
                        return ErrorContent.create(e);
                    }
                }
                
            }).filter(call);
        } catch (RuntimeException e) {
            return ErrorContent.create(e);
        } catch (FilterException e) {
            return ErrorContent.create(e);
        }
        /*CHECKSTYLE:ON*/ 
    }
    
    private void after(HttpRequest request) {
        for (HttpRequestFilter filter : requestFilters) {
            filter.after(request);
        }
    }
    
}
