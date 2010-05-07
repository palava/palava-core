package de.cosmocode.palava.core.inject;

import java.net.InetSocketAddress;

import org.junit.Assert;
import org.junit.Test;

import com.google.inject.TypeLiteral;

import de.cosmocode.junit.UnitProvider;

/**
 * Tests {@link InetSocketAddressConverter}.
 *
 * @since 2.4 
 * @author Willi Schoenborn
 */
public final class InetSocketAddressConverterTest implements UnitProvider<InetSocketAddressConverter> {

    private static final TypeLiteral<InetSocketAddress> LITERAL = TypeLiteral.get(InetSocketAddress.class);

    @Override
    public InetSocketAddressConverter unit() {
        return new InetSocketAddressConverter();
    }
    
    /**
     * Tests {@link InetSocketAddressConverter#convert(String, TypeLiteral)} with a valid input (hostname).
     */
    @Test
    public void hostname() {
        Assert.assertEquals(InetSocketAddress.createUnresolved("localhost", 8080), 
            unit().convert("localhost:8080", LITERAL));
    }

    /**
     * Tests {@link InetSocketAddressConverter#convert(String, TypeLiteral)} with a valid input (127.0.0.1).
     */
    @Test
    public void loopback() {
        Assert.assertEquals(InetSocketAddress.createUnresolved("127.0.0.1", 8080), 
            unit().convert("127.0.0.1:8080", LITERAL));
    }


    /**
     * Tests {@link InetSocketAddressConverter#convert(String, TypeLiteral)} with a valid input (0.0.0.0).
     */
    @Test
    public void all() {
        Assert.assertEquals(InetSocketAddress.createUnresolved("0.0.0.0", 8080), 
                unit().convert("0.0.0.0:8080", LITERAL));
    }
    
    /**
     * Tests {@link InetSocketAddressConverter#convert(String, TypeLiteral)} without port.
     */
    @Test(expected = IllegalArgumentException.class)
    public void noPort() {
        unit().convert("localhost", LITERAL);
    }
    
    /**
     * Tests {@link InetSocketAddressConverter#convert(String, TypeLiteral)} without a host.
     */
    @Test(expected = IllegalArgumentException.class)
    public void noHost() {
        unit().convert(":8080", LITERAL);
    }
    
}
