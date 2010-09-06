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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;

/**
 * Intercept the System.out and System.err to be logged with the real logging system.
 *
 * @since 2.8
 * @author Tobias Sarnowski
 */
final class SystemStreamWrapper {
    private static final Logger LOG = LoggerFactory.getLogger(SystemStreamWrapper.class);

    private static final Logger SYSTEM = LoggerFactory.getLogger("SYSTEM");

    private static final Charset CHARSET = Charset.forName("UTF-8");

    static void wrapSystemStreams() {
        System.setOut(new PrintStream(new StreamWrapper(WrappedTarget.INFO)));
        System.setErr(new PrintStream(new StreamWrapper(WrappedTarget.ERROR)));
    }

    private static enum WrappedTarget {
        INFO,
        ERROR
    }

    private static final class StreamWrapper extends OutputStream {
        private final WrappedTarget target;

        private final StringBuilder builder = new StringBuilder(500);

        public StreamWrapper(WrappedTarget target) {
            this.target = target;
        }

        private void log(Object message) {
            if (target == WrappedTarget.INFO) {
                SYSTEM.info("{}", message);
            } else if (target == WrappedTarget.ERROR) {
                SYSTEM.error("{}", message);
            } else {
                throw new UnsupportedOperationException("target " + target.name() + " not supported");
            }
        }

        @Override
        public void flush() throws IOException {
            if (builder.length() > 0) {
                log(builder);
                builder.setLength(0);
            }
        }

        /**
         * must-have implementation
         *
         * @param b
         * @throws IOException
         */
        @Override
        public void write(int b) throws IOException {
            if (b == '\n' || b == '\r') {
                flush();
            } else {
                builder.append((char)b);
            }
        }

        /**
         * overwritten for more performance
         *
         * @param bytes
         * @throws IOException
         */
        @Override
        public void write(byte[] bytes) throws IOException {
            if (bytes.length == 0) return;

            byte b = bytes[bytes.length - 1];
            if (bytes.length > 1 && (b == '\n' || b == '\r')) {

                int end = bytes.length - 1;

                b = bytes[bytes.length - 2];
                if (bytes.length > 2 && (b == '\n' || b == '\r')) {
                    end = bytes.length - 2;
                }

                builder.append(new String(bytes, CHARSET), 0, end);
                flush();
            } else {
                builder.append(new String(bytes, CHARSET));
            }
        }
    }

}