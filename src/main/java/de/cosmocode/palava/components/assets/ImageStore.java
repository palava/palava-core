package de.cosmocode.palava.components.assets;

import java.io.File;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hibernate.Session;
import org.jdom.Element;

import de.cosmocode.palava.ComponentException;
import de.cosmocode.palava.Server;
import de.cosmocode.palava.components.cstore.FSContentStore;

public class ImageStore extends AssetStore {

	File dir;
	private static final String PATTERN = "[a-zA-Z0-9]+";
	Pattern validFilterName = Pattern.compile(PATTERN);

	public File getFile( String storeKey, String filterName ){
		return new File(dir,filterName + "/" + storeKey);
	}
	
	public ImageManager createImageManager(Session session){
		return new ImageManager(this,store,session);
	}
    @Override
	public void configure(Element root, Server server)
			throws ComponentException {
	
		super.configure(root, server);

		
		dir = new File( server.getFilename(root.getChildText("filterdir")) );
		
		store = new FSContentStore();
		store.configure(root.getChild("store"), server);
				
		@SuppressWarnings("unchecked")
		List<Element> children = root.getChild("filters").getChildren("filter");
		
		for ( Element filter : children ) {
			String name = filter.getAttributeValue("name");
			
			// test name
			
			Matcher m = validFilterName.matcher(name);
			if ( ! m.matches())
				throw new ComponentException(
						"bad filter name " + name + " : should match " + validFilterName.pattern(),
						this);
			
		}
	}
	
	@Override
	public void initialize() throws Exception {
		super.initialize();
		if ( ! dir.exists() )
			dir.mkdirs();
	}


}
