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

package de.project.main;

import com.google.inject.Key;
import com.google.inject.name.Names;

import de.cosmocode.palava.components.cstore.ContentStore;
import de.cosmocode.palava.components.cstore.FSContentStore;
import de.cosmocode.palava.components.mail.Mailer;
import de.cosmocode.palava.components.mail.VelocityMailer;
import de.cosmocode.palava.core.CoreModule;
import de.cosmocode.palava.core.call.Call;
import de.cosmocode.palava.core.call.filter.Filter;
import de.cosmocode.palava.core.call.filter.FilterChain;
import de.cosmocode.palava.core.call.filter.FilterException;
import de.cosmocode.palava.core.command.CommandException;
import de.cosmocode.palava.core.inject.AbstractApplicationModule;
import de.cosmocode.palava.core.protocol.content.Content;

/**
 * Mock implementation which adds service bindings.
 *
 * @author Willi Schoenborn
 */
public final class ProjectModule extends AbstractApplicationModule {

    @Override
    protected void configureApplication() {
        install(new CoreModule());

        serve(Key.get(ContentStore.class, Names.named("FileSystem"))).with(FSContentStore.class);
        serve(Mailer.class).with(VelocityMailer.class);
        
        filter("*").through(new Filter() {
            
            @Override
            public Content filter(Call call, FilterChain chain) throws FilterException, CommandException {
                return chain.filter(call);
            }
            
        });
        
        alias("de.project.commands").as("@project");
        
    }

}
