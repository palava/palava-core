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

package de.cosmocode.palava.core.bridge.call.filter.definition;

import java.util.regex.Pattern;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;

import de.cosmocode.palava.core.bridge.command.Command;
import de.cosmocode.palava.core.bridge.command.Commands;

/**
 * A {@link CommandMatcher} which uses a regex matching algorithm.
 *
 * @author Willi Schoenborn
 */
final class RegexCommandNameMatcher implements Predicate<Command> {

    private final Pattern pattern;
    
    public RegexCommandNameMatcher(String pattern) {
        Preconditions.checkNotNull(pattern, "Pattern");
        this.pattern = Pattern.compile(pattern);
    }
    
    @Override
    public boolean apply(Command command) {
        return pattern.matcher(Commands.getClass(command).getName()).matches();
    }

}
