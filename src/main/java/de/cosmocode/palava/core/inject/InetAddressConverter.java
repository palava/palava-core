package de.cosmocode.palava.core.inject;

import java.net.InetAddress;
import java.net.UnknownHostException;

import com.google.inject.spi.TypeConverter;

/**
 * A {@link TypeConverter} for {@link InetAddress}es.
 *
 * @author Willi Schoenborn
 */
public final class InetAddressConverter extends AbstractTypeConverter<InetAddress> {

    @Override
    public InetAddress convert(String value) {
        try {
            return InetAddress.getByName(value);
        } catch (UnknownHostException e) {
            throw new IllegalArgumentException(e);
        }
    }
    
}
