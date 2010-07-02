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

package de.cosmocode.palava.core.inject;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.common.io.LineProcessor;
import com.google.inject.internal.Lists;
import com.google.inject.spi.TypeConverter;

/**
 * A {@link TypeConverter} for {@link List}s of {@link String}s.
 *
 * @since 2.6
 * @author Willi Schoenborn
 */
public final class StringListConverter extends AbstractTypeConverter<List<String>> {

    private static final Logger LOG = LoggerFactory.getLogger(StringListConverter.class);

    private final FileConverter converter = new FileConverter();
    
    @Override
    protected List<String> convert(String value) {
        final File file = converter.convert(value);
        
        try {
            LOG.trace("Reading lines from {}", file);
            return Files.readLines(file, Charsets.UTF_8, new Processor());
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }
    
    /**
     * A {@link LineProcessor} which strips comments and collects trimmed content.
     *
     * @since 2.5
     * @author Willi Schoenborn
     */
    private static final class Processor implements LineProcessor<List<String>> {
        
        private final List<String> list = Lists.newArrayList();
        
        @Override
        public boolean processLine(String line) throws IOException {
            final int hash = line.indexOf("#");
            
            final String content;
            
            if (hash == -1) {
                content = line;
            } else {
                content = line.substring(0, hash);
            }
            
            if (StringUtils.isNotBlank(content)) {
                final String trimmed = content.trim();
                LOG.trace("Parsed line '{}'", trimmed);
                list.add(trimmed);
            } else {
                LOG.trace("Skipping line: '{}'", line);
            }
            
            return true;
        }
        
        @Override
        public List<String> getResult() {
            return list;
        }
        
    }
    
}
