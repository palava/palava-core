package de.cosmocode.palava.jobs.assets;

import java.util.List;
import java.util.Map;

import org.json.JSONStringer;
import org.json.extension.JSONConstructor;

import de.cosmocode.palava.DataRequest;
import de.cosmocode.palava.JSONContent;
import de.cosmocode.palava.MissingArgumentException;
import de.cosmocode.palava.Request;
import de.cosmocode.palava.Response;
import de.cosmocode.palava.Server;
import de.cosmocode.palava.Session;
import de.cosmocode.palava.components.assets.Asset;
import de.cosmocode.palava.components.assets.Directory;
import de.cosmocode.palava.components.assets.ImageManager;
import de.cosmocode.palava.components.assets.ImageStore;
import de.cosmocode.palava.jobs.hib.HibJob;

public class getDirectoryLong extends HibJob {

	@Override
	public void process(Request req, Response response, Session session, Server server, 
			Map<String, Object> caddy, org.hibernate.Session hibSession) throws Exception {
		
        ImageStore store = server.components.lookup(ImageStore.class);
        if ( hibSession == null ) hibSession = createHibSession(server,caddy);

        DataRequest request = (DataRequest) req;
        final Map<String, String> map = request.getArguments();

        String dirID = map.get("id");
        if(dirID == null) throw new MissingArgumentException(this, "id");

        ImageManager manager = store.createImageManager(hibSession);

        Directory directory = manager.getDirectory(Long.parseLong(dirID));

        directory.sort(Asset.ByCreationDateComparator.INSTANCE);
        
        JSONConstructor json = new JSONStringer();

        List<Asset> assets = directory.getAssets();
        
        json.array();
        for(Asset asset : assets) {        	
        	json.
        		object();
		        	asset.encodeJSON(json);       	
		        	json.key("directories").
	    			value(manager.getDirectoryIdsForAsset(asset.getId())).
    			endObject();        	
        }
        json.endArray();
        
        response.setContent(new JSONContent(json));
	}
}