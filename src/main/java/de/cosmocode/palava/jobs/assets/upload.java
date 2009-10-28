package de.cosmocode.palava.jobs.assets;

import java.util.Map;

import de.cosmocode.palava.ConnectionLostException;
import de.cosmocode.palava.MimeType;
import de.cosmocode.palava.PHPContent;
import de.cosmocode.palava.Request;
import de.cosmocode.palava.RequestContent;
import de.cosmocode.palava.Response;
import de.cosmocode.palava.Server;
import de.cosmocode.palava.Session;
import de.cosmocode.palava.components.assets.Asset;
import de.cosmocode.palava.components.assets.ImageManager;
import de.cosmocode.palava.components.assets.ImageStore;
import de.cosmocode.palava.jobs.hib.HibJob;

public class upload extends HibJob {

	@Override
	public void process(Request request, Response response, Session session,
			Server server, Map<String, Object> caddy, org.hibernate.Session hibSession)
			throws ConnectionLostException, Exception {
		
        ImageStore ist = server.components.lookup(ImageStore.class);

        if ( hibSession == null ) hibSession = createHibSession(server,caddy);

        Asset asset = (Asset) caddy.get("asset");
        if ( asset == null ) throw new NullPointerException("asset == null");
        MimeType mimetype = (MimeType) caddy.get("mimetype");

        // just use the request data as the content
        asset.setContent( new RequestContent(request, mimetype) );

        ImageManager im = ist.createImageManager(hibSession);

        im.createAsset(asset);

		response.setContent( new PHPContent(asset.getId()) );
	}

}
