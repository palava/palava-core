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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package de.cosmocode.palava.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.spi.InjectionListener;

import de.cosmocode.palava.core.lifecycle.AutoStartable;

/**
 * {@link InjectionListener} which handles {@link AutoStartable}s.
 *
 * @author Willi Schoenborn
 * @param <I>
 */
public final class AutoStartableListener<I> implements InjectionListener<I> {

    private static final Logger LOG = LoggerFactory.getLogger(AutoStartableListener.class);

    @Override
    public void afterInjection(I injectee) {
        if (injectee instanceof AutoStartable) {
            LOG.info("Autostarting service {}", injectee);
            AutoStartable.class.cast(injectee).start();
        }
    }
    
}
