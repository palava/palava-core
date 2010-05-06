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

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Test;

import com.google.inject.Module;

/**
 * Tests {@link Palava}.
 *
 * @since 2.4 
 * @author Willi Schoenborn
 */
public final class PalavaTest {

    /**
     * Tests {@link Palava#newFramework()} with a missing application.properties.
     */
    @Test(expected = IllegalArgumentException.class)
    public void newFramework() {
        Palava.newFramework();
    }
    
    /**
     * Tests {@link Palava#newFramework(Module)} with a missing application.properties.
     */
    @Test(expected = IllegalArgumentException.class)
    public void newFrameworkModule() {
        Palava.newFramework(new EmptyApplication());
    }
    
    /**
     * Tests {@link Palava#newFramework(Module)} with a null module.
     */
    @Test(expected = NullPointerException.class)
    public void newFrameworkModuleNull() {
        final Module module = null;
        Palava.newFramework(module);
    }
    
    /**
     * Tests {@link Palava#newFramework(File)} with a valid file.
     */
    @Test
    public void newFrameworkFile() {
        Palava.newFramework(new File("src/test/resources/file.properties"));
    }
    
    /**
     * Tests {@link Palava#newFramework(File))} with a null file.
     */
    @Test(expected = NullPointerException.class)
    public void newFrameworFilekNull() {
        final File file = null;
        Palava.newFramework(file);
    }
    
    /**
     * Tests {@link Palava#newFramework(File)} with a missing file.
     */
    @Test(expected = IllegalArgumentException.class)
    public void newFrameworkFileMissing() {
        Palava.newFramework(new File("missing.properties"));
    }
    
    /**
     * Tests {@link Palava#newFramework(URL)} with valid url.
     * 
     * @throws MalformedURLException should not happen 
     */
    @Test
    public void newFrameworkUrl() throws MalformedURLException {
        Palava.newFramework(new URL("file:src/test/resources/file.properties"));
    }
    
    /**
     * Tests {@link Palava#newFramework(URL)} with a null url.
     */
    @Test(expected = NullPointerException.class)
    public void newFrameworkUrlNull() {
        final URL url = null;
        Palava.newFramework(url);
    }
    
    /**
     * Tests {@link Palava#newFramework(URL)} with a missing url.
     * 
     * @throws MalformedURLException should not happen
     */
    @Test(expected = IllegalArgumentException.class)
    public void newFrameworkUrlMissing() throws MalformedURLException {
        Palava.newFramework(new URL("file:src/test/resources/missing.properties"));
    }
    
    /**
     * Tests {@link Palava#newFramework(Module, File)} with a valid module and file.
     */
    @Test
    public void newFrameworkModuleFile() {
        Palava.newFramework(new EmptyApplication(), new File("src/test/resources/empty.properties"));
    }
    
    /**
     * Tests {@link Palava#newFramework(Module, File)} with a null module and a valid file.
     */
    @Test(expected = NullPointerException.class)
    public void newFrameworkModuleFileNullModule() {
        Palava.newFramework(null, new File("src/test/resources/empty.properties"));
    }
    
    /**
     * Tests {@link Palava#newFramework(Module, File)} with a valid module and a null file.
     */
    @Test(expected = NullPointerException.class)
    public void newFrameworkModuleFileNullFile() {
        final File file = null;
        Palava.newFramework(new EmptyApplication(), file);
    }

    /**
     * Tests {@link Palava#newFramework(Module, File)} with a null module and a null file.
     */
    @Test(expected = NullPointerException.class)
    public void newFrameworkModuleFileNullBoth() {
        final Module module = null;
        final File file = null;
        Palava.newFramework(module, file);
    }

    /**
     * Tests {@link Palava#newFramework(Module, File)} with a valid module and a missing file.
     */
    @Test(expected = IllegalArgumentException.class)
    public void newFrameworkModuleFileMissing() {
        Palava.newFramework(new EmptyApplication(), new File("src/test/resources/missing.properties"));
    }
    
    /**
     * Tests {@link Palava#newFramework(Module, URL)} with a valid module and a valid url.
     * 
     * @throws MalformedURLException should not happen 
     */
    @Test
    public void newFrameworkModuleURL() throws MalformedURLException {
        Palava.newFramework(new EmptyApplication(), new URL("file:src/test/resources/empty.properties"));
    }
    
    /**
     * Tests {@link Palava#newFramework(Module, URL)} with a null module and a valid url.
     * 
     * @throws MalformedURLException should not happen 
     */
    @Test(expected = NullPointerException.class)
    public void newFrameworkModuleURLNullModule() throws MalformedURLException {
        Palava.newFramework(null, new URL("file:src/test/resources/empty.properties"));
    }
    
    /**
     * Tests {@link Palava#newFramework(Module, URL)} with a valid module and a null url.
     */
    @Test(expected = NullPointerException.class)
    public void newFrameworkModuleURLNullURL() {
        final URL file = null;
        Palava.newFramework(new EmptyApplication(), file);
    }

    /**
     * Tests {@link Palava#newFramework(Module, URL)} with a null module and a null url.
     */
    @Test(expected = NullPointerException.class)
    public void newFrameworkModuleURLNullBoth() {
        final Module module = null;
        final URL file = null;
        Palava.newFramework(module, file);
    }

    /**
     * Tests {@link Palava#newFramework(Module, URL)} with a valid module and a missing url.
     * 
     * @throws MalformedURLException should not happen 
     */
    @Test(expected = IllegalArgumentException.class)
    public void newFrameworkModuleURLMissing() throws MalformedURLException {
        Palava.newFramework(new EmptyApplication(), new URL("file:src/test/resources/missing.properties"));
    }
    
}
