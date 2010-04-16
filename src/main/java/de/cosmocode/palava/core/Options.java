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
import java.util.List;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.Option;

import com.google.common.collect.Lists;

/**
 * A bean which holds the corresponding command line parameters.
 *
 * @author Willi Schoenborn
 */
final class Options {

    @Option(name = "-s", required = false, aliases = "--state-file", usage = "Path to state file")
    private File stateFile;

    @Option(name = "-n", required = false, aliases = "--no-auto-shutdown", 
        usage = "If the framework should shut down as soon as possible after boot")
    private boolean noAutoShutdown;
    
    @Argument(required = true, usage = "List of configuration files")
    private List<File> configs = Lists.newArrayList();

    public File getStateFile() {
        return stateFile;
    }
    
    public boolean isNoAutoShutdown() {
        return noAutoShutdown;
    }
    
    public List<File> getConfigs() {
        return configs;
    }
    
}
