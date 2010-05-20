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

import java.net.InetSocketAddress;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Preconditions;
import com.google.inject.spi.TypeConverter;

/**
 * A {@link TypeConverter} for {@link InetSocketAddress}es.
 *
 * @since 2.5
 * @author Willi Schoenborn
 */
public final class InetSocketAddressConverter extends AbstractTypeConverter<InetSocketAddress> {

    private static final Pattern PATTERN = Pattern.compile("^([^:]+):(\\d+)$");
    private static final String WILDCARD = "*";
    
    @Override
    protected InetSocketAddress convert(String value) {
        final Matcher matcher = PATTERN.matcher(value);
        Preconditions.checkArgument(matcher.matches(), "%s does not match %s", value, PATTERN);
        final String host = matcher.group(1);
        final int port = Integer.parseInt(matcher.group(2));
        return WILDCARD.equals(host) ? new InetSocketAddress(port) : new InetSocketAddress(host, port);
    }
    
}
