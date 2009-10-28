package de.cosmocode.palava.components.hib;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.jdom.Element;

import de.cosmocode.palava.Component;
import de.cosmocode.palava.ComponentException;
import de.cosmocode.palava.ComponentManager;
import de.cosmocode.palava.Server;

public class Hib implements Component {

    SessionFactory sessionFactory;

    @Override
    public void configure(Element root, Server server) throws ComponentException {
        
        String hibernateSchema = root.getChildText("hibernateSchema");
        if (hibernateSchema == null ) throw new NullPointerException("missing config entry 'hibernateSchema'");
        String hibernateConfiguration = root.getChildText("hibernateConfiguration");
        if (hibernateConfiguration == null ) throw new NullPointerException("missing config entry 'hibernateConfiguration'");
        
        
        hibernateConfiguration = server.getFilename( hibernateConfiguration );
        if ( ! hibernateSchema.startsWith("file://") ) {
            hibernateSchema = "file://" + server.getFilename( hibernateSchema );
        }

        AnnotationConfiguration cfg = new AnnotationConfiguration();
        try {
			cfg.addURL(new URL(hibernateSchema));
		} catch (MalformedURLException e) {
			throw new ComponentException(e.getMessage(), this);
		}
        cfg.configure(new File(hibernateConfiguration));
        sessionFactory = cfg.buildSessionFactory();

    }

    @Override
    public void compose(ComponentManager manager) throws ComponentException {
    	
    }
    
    @Override
    public void initialize() throws Exception {
        
    }
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

}

