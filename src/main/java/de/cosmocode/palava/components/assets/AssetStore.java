package de.cosmocode.palava.components.assets;

import org.hibernate.Session;
import org.jdom.Element;

import de.cosmocode.palava.Component;
import de.cosmocode.palava.ComponentException;
import de.cosmocode.palava.ComponentManager;
import de.cosmocode.palava.Server;
import de.cosmocode.palava.components.cstore.FSContentStore;

public class AssetStore implements Component {

	FSContentStore store;
	
	public AssetManager createAssetManager(Session session) {
		return new AssetManager(store,session);
	}

	@Override
	public void compose(ComponentManager manager) throws ComponentException {
		store.compose(manager);
	}

	@Override
	public void configure(Element root, Server server)
			throws ComponentException {
	
		store = new FSContentStore();
		store.configure(root.getChild("store"), server);
	}

	@Override
	public void initialize() throws Exception {
		store.initialize();
	}
	


}
