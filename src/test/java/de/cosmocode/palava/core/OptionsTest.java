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
