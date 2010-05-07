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
    
    @Override
    protected InetSocketAddress convert(String value) {
        final Matcher matcher = PATTERN.matcher(value);
        Preconditions.checkArgument(matcher.matches(), "%s does not match %s", value, PATTERN);
        final String host = matcher.group(1);
        final int port = Integer.parseInt(matcher.group(2));
        return new InetSocketAddress(host, port);
    }
    
}
