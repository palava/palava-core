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

package de.cosmocode.palava.jobs.session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Singleton;

import de.cosmocode.palava.core.call.Call;
import de.cosmocode.palava.core.command.Command;
import de.cosmocode.palava.core.command.CommandException;
import de.cosmocode.palava.core.protocol.content.Content;
import de.cosmocode.palava.core.protocol.content.PhpContent;
import de.cosmocode.palava.core.session.HttpSession;

/**
 * Deletes the actual session.
 * 
 * @author Tobias Sarnowski
 */
@Singleton
public class destroy implements Command {

    private static final Logger log = LoggerFactory.getLogger(destroy.class);
    
    @Override
    public Content execute(Call call) throws CommandException {
        final HttpSession session = call.getHttpRequest().getHttpSession();
        if (session == null) {
            log.warn("No session found, cant destroy.");
        } else {
            log.debug("Destroying sessiong {}", session);
            session.destroy();
        }
        return PhpContent.OK;
    }
    
}
