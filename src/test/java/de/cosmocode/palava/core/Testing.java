package de.cosmocode.palava.core;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

public final class Testing {

    private static final Logger LOG = LoggerFactory.getLogger(Testing.class);
    
    public static Framework load() {
        final File file = new File("src/test/resources/application.properties");
        final Properties properties = new Properties();
        
        if (file.exists()) {
            LOG.info("Using {}", file);
            try {
                final Reader reader = new FileReader(file);
                try {
                    properties.load(reader);
                } finally {
                    reader.close();
                }
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        } else {
            LOG.info("{} does not exist, looking for classpath resources", file);
            final InputStream stream = Testing.class.getClassLoader().getResourceAsStream("application.properties");
            Preconditions.checkState(stream != null, "No application.properties found");
            try {
                try {
                    properties.load(stream);
                } finally {
                    stream.close();
                }
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }
        
        return Palava.newFramework(properties);
    }

}
