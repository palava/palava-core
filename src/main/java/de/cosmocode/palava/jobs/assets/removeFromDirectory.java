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

public class removeFromDirectory extends HibJob {

	@Override
	public void process(Request req, Response resp, Session session,
			Server server, Map<String, Object> caddy,
			org.hibernate.Session hibSession) throws Exception {
		
        ImageStore ist = server.components.lookup(ImageStore.class);
        if ( hibSession == null ) hibSession = createHibSession(server,caddy);

        DataRequest request = (DataRequest) req;
        final Map<String, String> map = request.getArguments();

        String dirId = map.get("dirId");
        if ( dirId == null ) throw new MissingArgumentException(this, "dirId");

        String assetId = map.get("assetId");
        if ( assetId == null ) throw new MissingArgumentException(this,"assetId");

        ImageManager im = ist.createImageManager(hibSession);

        Boolean done = im.removeAssetFromDirectory(Long.parseLong(dirId),Long.parseLong(assetId));

		resp.setContent( new PHPContent(done) );
	}

	

}
