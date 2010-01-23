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

import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.Injector;

import de.cosmocode.palava.core.server.Server;
import de.cosmocode.palava.legacy.Job;

/**
 * Default implementation of the {@link CommandManager} interface.
 *
 * @author Willi Schoenborn
 */
final class DefaultCommandManager implements CommandManager {

    private static final Logger log = LoggerFactory.getLogger(DefaultCommandManager.class);

    private final Injector injector;
    
    private final Server server;
    
    private final Set<Alias> aliases;
    
    private final Map<String, Class<?>> cache = Maps.newHashMap();
    
    @Inject
    public DefaultCommandManager(Injector injector, Server server, Set<Alias> aliases) {
        this.injector = Preconditions.checkNotNull(injector, "Injector");
        this.server = Preconditions.checkNotNull(server, "Server");
        this.aliases = Preconditions.checkNotNull(aliases, "Aliases");
        log.debug("Aliases {}", aliases);
    }
    
    @Override
    public Command forName(String aliasedName) {
        Preconditions.checkNotNull(aliasedName, "AliasedName");
        final String realName = realName(aliasedName);
        
        final Class<?> type;
        final Class<?> cached = cache.get(realName);
        if (cached == null) {
            try {
                type = Class.forName(realName);
            } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException(e);
            }
            cache.put(realName, type);
        } else {
            type = cached;
        }
        if (Job.class.isAssignableFrom(type)) {
            final Job job = injector.getInstance(type.asSubclass(Job.class));
            return new JobCommand(server, job);
        } else {
            return injector.getInstance(type.asSubclass(Command.class));
        }
    }

    private String realName(String aliasedName) {
        for (Alias alias : aliases) {
            if (aliasedName.startsWith(alias.getName())) {
                return alias.apply(aliasedName);
            }
        }
        return aliasedName;
    }
    
}
