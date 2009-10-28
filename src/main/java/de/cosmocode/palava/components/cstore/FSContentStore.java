package de.cosmocode.palava.components.cstore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;
import org.jdom.Element;

import de.cosmocode.palava.Component;
import de.cosmocode.palava.ComponentException;
import de.cosmocode.palava.ComponentManager;
import de.cosmocode.palava.Content;
import de.cosmocode.palava.FileContent;
import de.cosmocode.palava.MimeType;
import de.cosmocode.palava.Server;
import de.cosmocode.palava.StreamContent;

public class FSContentStore implements ContentStore, Component {
	
	File dir;
	
	private static final Logger logger = Logger.getLogger( FSContentStore.class );
	@Override
	public StreamContent load(String key) throws Exception {
        File file = mkFile(key);

        if ( ! file.exists() ) return null;
		return new FileContent( file );
	}
	String generateFilename(MimeType mimeType) {
		return DigestUtils.md5Hex( ""+ System.currentTimeMillis() ) + "." + mimeType.getMinor();
	}

    private File mkFile( String name ) {
        return new File(dir, name );
    }

	@Override
	public String store(Content content) throws Exception {
		
		String name = generateFilename(content.getMimeType());
		File file = mkFile(name);
		
		FileOutputStream out = new FileOutputStream(file);
		content.write(out);
		out.flush();
		out.close();
		return name;
	}
	
	public String store( InputStream in, MimeType mimeType ) throws Exception {
		String name = generateFilename(mimeType);
		File file = mkFile(name);
		
		FileOutputStream out = new FileOutputStream(file);
		
		StreamContent.copy(in,out);
		out.flush();
		out.close();
		return name;
		
	}

	@Override
	public void compose(ComponentManager manager) throws ComponentException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void configure(Element root, Server server)
			throws ComponentException {
		dir = new File( server.getFilename( root.getChildText("root") ) );		
	}

	@Override
	public void initialize() throws Exception {
		
	}
	@Override
	public void remove(String key) {
		File file = mkFile(key);
		
		if ( ! file.delete() )
			logger.error("cannot delete " + file.getAbsolutePath());
		
		
	}

}
