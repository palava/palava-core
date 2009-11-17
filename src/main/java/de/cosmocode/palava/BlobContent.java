package de.cosmocode.palava;

import java.sql.Blob;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * mysql blob stream
 * @author Tobias Sarnowski
 */

public class BlobContent extends StreamContent {
    
    private static final Logger log = LoggerFactory.getLogger(BlobContent.class);
    
	public BlobContent(Blob blob, MimeType mime) throws Exception {
		super(blob.getBinaryStream(), blob.length(), mime);
		log.info("blob created, mime=" + mime.toString() + " size=" + blob.length() );
	}
	
}