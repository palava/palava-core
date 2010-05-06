package de.cosmocode.palava.core.inject;

import java.net.URI;
import java.net.URISyntaxException;

import org.junit.Assert;
import org.junit.Test;

import com.google.inject.TypeLiteral;

import de.cosmocode.junit.UnitProvider;

/**
 * Tests {@link URIConverter}.
 *
 * @since 2.4
 * @author Willi Schoenborn
 */
public final class URIConverterTest implements UnitProvider<URIConverter> {

    private static final TypeLiteral<URI> LITERAL = TypeLiteral.get(URI.class);

    @Override
    public URIConverter unit() {
        return new URIConverter();
    }
    
    /**
     * Tests {@link URIConverter#convert(String, TypeLiteral)} with a http uri.
     * 
     * @throws URISyntaxException should not happen 
     */
    @Test
    public void http() throws URISyntaxException {
        Assert.assertEquals(new URI("http://www.google.de"), unit().convert("http://www.google.de", LITERAL));
    }

    /**
     * Tests {@link URLConverter#convert(String, TypeLiteral)} with a ftp url.
     * 
     * @throws URISyntaxException should not happen 
     */
    @Test
    public void ftp() throws URISyntaxException {
        Assert.assertEquals(new URI("ftp://google.de"), unit().convert("ftp://google.de", LITERAL));
    }

    /**
     * Tests {@link URIConverter#convert(String, TypeLiteral)} with a file URI.
     * 
     * @throws URISyntaxException should not happen 
     */
    @Test
    public void file() throws URISyntaxException {
        Assert.assertEquals(new URI("file:some.file"), unit().convert("file:some.file", LITERAL));
    }

}
