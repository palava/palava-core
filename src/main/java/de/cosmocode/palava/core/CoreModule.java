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

package de.cosmocode.palava.core;

import com.google.inject.Binder;
import com.google.inject.Module;

import de.cosmocode.palava.core.bridge.call.filter.FilterModule;
import de.cosmocode.palava.core.bridge.command.CommandModule;
import de.cosmocode.palava.core.bridge.request.RequestModule;
import de.cosmocode.palava.core.bridge.scope.ScopeModule;
import de.cosmocode.palava.core.bridge.session.SessionModule;
import de.cosmocode.palava.core.bridge.simple.ProtocolModule;
import de.cosmocode.palava.core.bridge.simple.socket.DefaultListenerModule;
import de.cosmocode.palava.core.concurrent.ConcurrencyModule;
import de.cosmocode.palava.core.inject.InjectionModule;
import de.cosmocode.palava.core.registry.RegistryModule;
import de.cosmocode.palava.legacy.server.ServerModule;

/**
 * The default module which installs the whole palava
 * core including all default implementations.
 *
 * @author Willi Schoenborn
 */
public final class CoreModule implements Module {

    @Override
    public void configure(Binder binder) {
        binder.install(new CommandModule());
        binder.install(new ConcurrencyModule());
        binder.install(new FilterModule());
        binder.install(new InjectionModule());
        binder.install(new ProtocolModule());
        binder.install(new RegistryModule());
        binder.install(new RequestModule());
        binder.install(new ScopeModule());
        binder.install(new ServerModule());
        binder.install(new SessionModule());
        binder.install(new DefaultListenerModule());
    }

}
