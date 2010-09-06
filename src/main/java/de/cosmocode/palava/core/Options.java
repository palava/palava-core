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

import com.google.common.collect.Lists;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.Option;

import java.io.File;
import java.util.List;

/**
 * A bean which holds the corresponding command line parameters.
 *
 * @since 2.3
 * @author Willi Schoenborn
 * @author Tobias Sarnowski
 */
final class Options {

    @Option(name = "-s", required = false, aliases = "--state-file", usage = "Path to state file")
    private File stateFile;

    @Option(name = "-n", required = false, aliases = "--no-auto-shutdown", 
        usage = "If the framework should shut down as soon as possible after boot")
    private boolean noAutoShutdown;

    @Option(name = "-i", required = false, aliases = "--intercept-streams",
        usage = "If the framework should pipe sysout and syserr to the logging system")
    private boolean interceptStreams = true;
    
    @Argument(required = true, usage = "List of configuration files")
    private List<File> configs = Lists.newArrayList();

    public File getStateFile() {
        return stateFile;
    }

    public boolean isNoAutoShutdown() {
        return noAutoShutdown;
    }

    public boolean isInterceptStreams() {
        return interceptStreams;
    }

    public List<File> getConfigs() {
        return configs;
    }

    @Override
    public String toString() {
        return String.format("Options [configs=%s, noAutoShutdown=%s, stateFile=%s]",
            configs, noAutoShutdown, stateFile);
    }
}
