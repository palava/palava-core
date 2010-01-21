package de.cosmocode.palava.core.inject;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.AbstractMatcher;
import com.google.inject.matcher.Matcher;
import com.google.inject.spi.TypeConverter;

/**
 * A {@link Module} for custom {@link TypeConverter}s and injection
 * related bindings.
 *
 * @author Willi Schoenborn
 */
public final class InjectionModule implements Module {

    private static final Logger log = LoggerFactory.getLogger(InjectionModule.class);
    
    private static final Matcher<TypeLiteral<?>> FILE_MATCHER = new AbstractMatcher<TypeLiteral<?>>() {

        @Override
        public boolean matches(TypeLiteral<?> literal) {
            return File.class.isAssignableFrom(literal.getRawType());
        }
        
    };
    
    private static final TypeConverter FILE_CONVERTER = new TypeConverter() {
        
        @Override
        public Object convert(String name, TypeLiteral<?> literal) {
            return new File(name);
        }
        
    };

    @Override
    public void configure(Binder binder) {
        log.debug("Registering file type converter");
        binder.convertToTypes(FILE_MATCHER, FILE_CONVERTER);
    }

}
