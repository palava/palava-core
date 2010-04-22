/**
 * Copyright 2010 CosmoCode GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
        return Palava.newFramework(properties);
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
