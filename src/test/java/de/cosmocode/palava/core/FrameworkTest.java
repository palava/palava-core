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

import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;

import com.google.inject.Stage;

import de.cosmocode.junit.UnitProvider;

/**
 * Tests {@link Main}.
 *
 * @author Willi Schoenborn
 */
public final class FrameworkTest implements UnitProvider<Framework> {

    @Override
    public Framework unit() {
        final Properties properties = new Properties();
        properties.setProperty(CoreConfig.APPLICATION, EmptyApplication.class.getName());
        return unit(properties);
    }
    
    private Framework unit(Properties properties) {
        return Palava.createFramework(properties);
    }
    
    /**
     * Tests {@link Framework#start()}.
     */
    @Test
    public void start() {
        final Framework unit = unit();
        
        unit.start();
        Assert.assertTrue("Framework should run", unit.isRunning());
        
        unit.stop();
        Assert.assertFalse("Framework should not run", unit.isRunning());
    }
    
    /**
     * Tests {@link Framework#getInstance(Class)}.
     */
    @Test
    public void getInstance() {
        Assert.assertNotNull(unit().getInstance(Registry.class));
    }
    
    /**
     * Tests whether the default {@link Stage} is {@link Stage#PRODUCTION}.
     */
    @Test
    public void stageProduction() {
        Assert.assertSame(Stage.PRODUCTION, unit().getInstance(Stage.class));
    }
    
    /**
     * Tests the configurability of the {@link Stage}.
     */
    @Test
    public void stageConfigurable() {
        final Properties properties = new Properties();
        properties.setProperty(CoreConfig.APPLICATION, EmptyApplication.class.getName());
        properties.setProperty(CoreConfig.STAGE, Stage.DEVELOPMENT.name());
        Assert.assertSame(Stage.DEVELOPMENT, unit(properties).getInstance(Stage.class));
    }

}
