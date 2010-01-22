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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import de.cosmocode.palava.core.call.Call;
import de.cosmocode.palava.core.call.filter.FilterChain;
import de.cosmocode.palava.core.call.filter.FilterChainFactory;
import de.cosmocode.palava.core.call.filter.FilterException;
import de.cosmocode.palava.core.command.Command;
import de.cosmocode.palava.core.command.CommandException;
import de.cosmocode.palava.core.command.CommandManager;
import de.cosmocode.palava.core.protocol.CallType;
import de.cosmocode.palava.core.protocol.Header;
import de.cosmocode.palava.core.protocol.ProtocolAlgorithm;
import de.cosmocode.palava.core.protocol.ProtocolException;
import de.cosmocode.palava.core.protocol.content.Content;
import de.cosmocode.palava.core.protocol.content.ErrorContent;
import de.cosmocode.palava.core.request.HttpRequest;
import de.cosmocode.palava.core.request.HttpRequestFactory;
import de.cosmocode.palava.core.request.HttpRequestFilter;
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
    
    private HttpRequest open(InputStream input, OutputStream output) {
        final Header header = algorithm.read(input);
        if (header.getCallType() == CallType.OPEN) {
            final String sessionId = header.getSessionId();
            final HttpSession session = sessionManager.get(sessionId);
            log.debug("Session found for id {}: {}", sessionId, session);
            if (session != null) {
                log.debug("Updating access time for {}", sessionId);
                session.updateAccessTime();
            }
            
            final Map<String, String> serverVariable = algorithm.open(header, input, output);
            return requestFactory.create(session, serverVariable);
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
        final HttpRequest request = open(input, output);
        before(request);
        while (true) {
            log.debug("Reading header");
            final Header header = algorithm.read(input);
            log.debug("Incoming call {}", header.getCallType());
            if (header.getCallType() == CallType.CLOSE) break;
            
            final Content content = process(request, header, input);
            
            try {
                algorithm.sendTo(content, output);
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }
        after(request);
    }
    
    private Content process(HttpRequest request, Header header, InputStream input) {
        final String aliasedName = header.getAliasedName();
        
        final Command command;
        
        try {
            command = commandManager.forName(aliasedName);
        /*CHECKSTYLE:OFF*/
        } catch (RuntimeException e) {
        /*CHECKSTYLE:ON*/
            return ErrorContent.create(e);
        }
        
        final Call call = header.getCallType().createCall(request, command, header, input);
        return filterAndExecute(command, call);
    }
    
    private Content filterAndExecute(final Command command, Call call) {
        try {
            return chainFactory.create(new FilterChain() {
                
                @Override
                public Content filter(Call call) throws FilterException {
                    try {
                        return command.execute(call);
                    } catch (CommandException e) {
                        log.error("Command execution failed", e);
                        return ErrorContent.create(e);
                    }
                }
                
            }).filter(call);
        /*CHECKSTYLE:OFF*/
        } catch (RuntimeException e) {
        /*CHECKSTYLE:ON*/ 
            log.error("Command execution failed", e);
            return ErrorContent.create(e);
        } catch (FilterException e) {
            log.error("Filtering failed", e);
            return ErrorContent.create(e);
        } finally {
            try {
                call.discard();
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }
    }
    
    private void after(HttpRequest request) {
        for (HttpRequestFilter filter : requestFilters) {
            filter.after(request);
        }
        log.debug("Destroying request {}", request);
        request.destroy();
    }
    
}
