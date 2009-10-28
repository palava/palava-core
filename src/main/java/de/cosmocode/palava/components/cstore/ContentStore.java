package de.cosmocode.palava.components.cstore;

import de.cosmocode.palava.Content;
import de.cosmocode.palava.StreamContent;

public interface ContentStore {

	public String store( Content content ) throws Exception;
	
	public StreamContent load( String key ) throws Exception;

	public void remove(String key);
	
}
