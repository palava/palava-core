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

import javax.servlet.Servlet;

import de.cosmocode.palava.core.call.Call;
import de.cosmocode.palava.core.protocol.Content;

/**
 * A {@link Command} is very similiar to a {@link Servlet}. It represents
 * a unit of work which will handle an incoming call.
 *
 * @author Willi Schoenborn
 */
public interface Command {

    /**
     * Executes an incoming call.
     * 
     * @param call the incoming call
     * @return the produced content
     * @throws CommandException if an error occurs during execution
     */
    Content execute(Call call) throws CommandException;
    
}
