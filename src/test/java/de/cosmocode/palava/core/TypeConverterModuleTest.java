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

import java.io.File;
import java.net.URL;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.ConfigurationException;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.name.Names;

import de.cosmocode.palava.core.inject.TypeConverterModule;

/**
 * Tests {@link TypeConverterModule}.
 *
 * @author Willi Schoenborn
 */
public final class TypeConverterModuleTest {

    private static final Module MODULE = new AbstractModule() {
        
        @Override
        protected void configure() {
            bindConstant().annotatedWith(Names.named("file.present")).to("file:src/test/resources/present.file");
            bindConstant().annotatedWith(Names.named("file.missing")).to("file:missing.file");
            bindConstant().annotatedWith(Names.named("file.non-local")).to("http://www.google.de/index.html");
            bindConstant().annotatedWith(Names.named("url")).to("http://www.google.de");
            bindConstant().annotatedWith(Names.named("properties.present.file")).to(
                "file:src/test/resources/present.properties");
            bindConstant().annotatedWith(Names.named("properties.present.cp")).to(
                "classpath:present.properties");
            bindConstant().annotatedWith(Names.named("properties.present.http")).to(
                "http://svn.apache.org/repos/asf/incubator/shiro/trunk/core/src/test/resources/log4j.properties");
            bindConstant().annotatedWith(Names.named("properties.missing.file")).to("file:missing.properties");
            bindConstant().annotatedWith(Names.named("properties.missing.cp")).to("classpath:missing.properties");
            bindConstant().annotatedWith(Names.named("properties.missing.http")).to(
                "http://www.missing.com/properties");
        }
        
    };

    private static final Injector INJECTOR = Guice.createInjector(new TypeConverterModule(), MODULE);
    
    /**
     * Tests type conversion of a file which exists.
     */
    @Test
    public void filePresent() {
        final File file = INJECTOR.getInstance(Key.get(File.class, Names.named("file.present")));
        Assert.assertNotNull(file);
        Assert.assertTrue(file.exists());
    }
    
    /**
     * Tests type conversion of a file which does not exist.
     */
    @Test
    public void fileMissing() {
        final File file = INJECTOR.getInstance(Key.get(File.class, Names.named("file.missing")));
        Assert.assertNotNull(file);
        Assert.assertFalse(file.exists());
    }
    
    /**
     * Tests type conversion of a file which is not local.
     */
    @Test
    public void fileNonLocal() {
        try {
            INJECTOR.getInstance(Key.get(File.class, Names.named("file.non-local")));
        } catch (ConfigurationException e) {
            Assert.assertEquals(1, e.getErrorMessages().size());
            return;
        }
        Assert.fail("Creation should fail");
    }
    
    /**
     * Tests type conversion of a http url.
     */
    @Test
    public void url() {
        final URL url = INJECTOR.getInstance(Key.get(URL.class, Names.named("url")));
        Assert.assertEquals("http://www.google.de", url.toExternalForm());
    }

    /**
     * Tests type conversion of a properties file which is present.
     */
    @Test
    public void propertiesFilePresent() {
        final Properties properties = INJECTOR.getInstance(Key.get(Properties.class, 
            Names.named("properties.present.file")));
        Assert.assertFalse(properties.isEmpty());
        Assert.assertEquals("value", properties.getProperty("key"));
    }

    /**
     * Tests type conversion of a properties classpath resource which is present.
     */
    @Test
    public void propertiesClasspathPresent() {
        final Properties properties = INJECTOR.getInstance(Key.get(Properties.class, 
                Names.named("properties.present.cp")));
        Assert.assertFalse(properties.isEmpty());
        Assert.assertEquals("value", properties.getProperty("key"));
    }

    /**
     * Tests type conversion of a properties http file which is present.
     */
    @Test
    public void propertiesHttpPresent() {
        final Properties properties = INJECTOR.getInstance(Key.get(Properties.class, 
            Names.named("properties.present.http")));
        Assert.assertFalse(properties.isEmpty());
        Assert.assertEquals("org.apache.log4j.ConsoleAppender", properties.getProperty("log4j.appender.stdout"));
    }
    
    /**
     * Tests type conversion of a properties file which does not exist.
     */
    @Test
    public void propertiesFileMissing() {
        try {
            INJECTOR.getInstance(Key.get(Properties.class, Names.named("properties.missing.file")));
        } catch (ConfigurationException e) {
            Assert.assertEquals(1, e.getErrorMessages().size());
            return;
        }
        Assert.fail("Creation should fail");
    }
    
    /**
     * Tests type conversion of a properties classpath resource which does not exist.
     */
    @Test
    public void propertiesClasspathMissing() {
        try {
            INJECTOR.getInstance(Key.get(Properties.class, Names.named("properties.missing.cp")));
        } catch (ConfigurationException e) {
            Assert.assertEquals(1, e.getErrorMessages().size());
            return;
        }
        Assert.fail("Creation should fail");
    }
    
    /**
     * Tests type conversion of a properties http file which does not exist.
     */
    @Test
    public void propertiesHttpMissing() {
        try {
            INJECTOR.getInstance(Key.get(Properties.class, Names.named("properties.missing.http")));
        } catch (ConfigurationException e) {
            Assert.assertEquals(1, e.getErrorMessages().size());
            return;
        }
        Assert.fail("Creation should fail");
    }
    
}
