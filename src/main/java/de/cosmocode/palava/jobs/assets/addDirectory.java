package de.cosmocode.palava.jobs.assets;

import java.util.Map;

import de.cosmocode.palava.DataRequest;
import de.cosmocode.palava.MissingArgumentException;
import de.cosmocode.palava.PHPContent;
import de.cosmocode.palava.Request;
import de.cosmocode.palava.Response;
import de.cosmocode.palava.Server;
import de.cosmocode.palava.Session;
import de.cosmocode.palava.components.assets.ImageManager;
import de.cosmocode.palava.components.assets.ImageStore;
import de.cosmocode.palava.jobs.hib.HibJob;

public class addDirectory extends HibJob {

	@Override
	public void process(Request req, Response resp, Session session,
			Server server, Map<String, Object> caddy,
			org.hibernate.Session hibSession) throws Exception {
		
        ImageStore ist = server.components.lookup(ImageStore.class);
        if ( hibSession == null ) hibSession = createHibSession(server,caddy);

        DataRequest request = (DataRequest) req;
        final Map<String, String> map = request.getArguments();

        String dirId = map.get("id");
        String name = map.get("name");
        if ( dirId == null && name == null ) throw new MissingArgumentException(this, "id or name");

        String assetId = map.get("assetId");
        if ( assetId == null ) throw new MissingArgumentException(this,"assetId");

        ImageManager im = ist.createImageManager(hibSession);

        Long resultId = im.addAssetToDirectory((dirId != null) ? Long.parseLong(dirId) : null, name, Long.parseLong(assetId) ).getId();

		resp.setContent( new PHPContent(resultId) );
	}

	

}
