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

package de.cosmocode.palava.core.command;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;

import com.google.common.base.Preconditions;
import com.google.inject.internal.Maps;

import de.cosmocode.palava.CloseConnection;
import de.cosmocode.palava.Job;
import de.cosmocode.palava.core.call.Call;
import de.cosmocode.palava.core.protocol.Response;
import de.cosmocode.palava.core.protocol.content.Content;
import de.cosmocode.palava.core.request.HttpRequest;
import de.cosmocode.palava.core.server.Server;
import de.cosmocode.palava.core.session.HttpSession;
import de.cosmocode.patterns.Adapter;

/**
 * {@link Job} to {@link Command} adapter.
 *
 * @author Willi Schoenborn
 */
@Adapter(Command.class)
final class JobCommand implements Command {

    private final Server server;
        
    private final Job job;
    
    public JobCommand(Server server, Job job) {
        this.server = Preconditions.checkNotNull(server, "Server");
        this.job = Preconditions.checkNotNull(job, "Job");
    }
    
    @Override
    public Content execute(Call call) throws CommandException {
        
        final Response response = new DummyResponse();
        final HttpRequest request = call.getHttpRequest();
        final HttpSession session = null;
        
        final String key = DigestUtils.md5Hex(Long.toString(System.nanoTime()));
        Map<String, Object> caddy = Maps.newHashMap();
        
        if (caddy == null) {
            caddy = Maps.newHashMap();
            request.set(key, caddy);
        }
        
        try {
            job.process(call, response, session, server, caddy);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new CommandException(e);
        }
        
        Preconditions.checkState(response.hasContent(), "No content set");
        
        return response.getContent();
    }
    
    private static class DummyResponse implements Response {
        
        private Content content;
        
        @Override
        public boolean hasContent() {
            return content != null;
        }
        
        @Override
        public Content getContent() {
            return content;
        }

        @Override
        public void setContent(Content content) {
            this.content = content; 
        }
        
        @Override
        public boolean sent() {
            return false;
        }
        
        @Override
        public void send() throws IOException {
            throw CloseConnection.getInstance();
        }
        
    }
    
}
