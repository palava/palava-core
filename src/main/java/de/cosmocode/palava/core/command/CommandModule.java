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

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.multibindings.Multibinder;


/**
 * A {@link Module} for the {@link de.cosmocode.palava.core.command} package.
 *
 * @author Willi Schoenborn
 */
public final class CommandModule implements Module {

    @Override
    public void configure(Binder binder) {
        Multibinder.newSetBinder(binder, Alias.class).addBinding().toInstance(
            Aliases.of("@palava", "de.cosmocode.palava.jobs")
        );
        binder.bind(CommandManager.class).to(DefaultCommandManager.class);
    }

}