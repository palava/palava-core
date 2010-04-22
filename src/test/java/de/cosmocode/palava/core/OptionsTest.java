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

import org.junit.Assert;
import org.junit.Test;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import com.google.inject.internal.Lists;

/**
 * Tests {@link Options}.
 *
 * @author Willi Schoenborn
 */
public final class OptionsTest {

    /**
     * Tests whether {@link Options} works as expected.
     * 
     * @throws CmdLineException should not happen 
     */
    @Test
    public void configure() throws CmdLineException {
        final Options unit = new Options();
        final CmdLineParser parser = new CmdLineParser(unit);
        final String[] arguments = {
            "--state-file", "state-file",
            "--no-auto-shutdown",
            "file1", "file2", "file3"
        };
        parser.parseArgument(arguments);
        
        Assert.assertEquals(new File("state-file"), unit.getStateFile());
        Assert.assertEquals(true, unit.isNoAutoShutdown());
        Assert.assertEquals(
            Lists.newArrayList(new File("file1"), new File("file2"), new File("file3")), 
            unit.getConfigs()
        );
    }

    /**
     * Tests whether {@link Options} requires configuration files.
     * 
     * @throws CmdLineException should not happen 
     */
    @Test(expected = CmdLineException.class)
    public void configureNoFiles() throws CmdLineException {
        new CmdLineParser(new Options()).parseArgument(new String[] {});
    }

}
