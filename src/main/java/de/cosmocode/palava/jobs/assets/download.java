package de.cosmocode.palava.jobs.assets;

import java.util.Map;

import de.cosmocode.palava.ConnectionLostException;
import de.cosmocode.palava.DataRequest;
import de.cosmocode.palava.MissingArgumentException;
import de.cosmocode.palava.PHPContent;
import de.cosmocode.palava.Request;
import de.cosmocode.palava.Response;
import de.cosmocode.palava.Server;
import de.cosmocode.palava.Session;
import de.cosmocode.palava.components.assets.Asset;
import de.cosmocode.palava.components.assets.ImageManager;
import de.cosmocode.palava.components.assets.ImageStore;
import de.cosmocode.palava.jobs.hib.HibJob;

public class download extends HibJob {

	@Override
	public void process(Request request, Response response, Session session,
			Server server, Map<String, Object> caddy, org.hibernate.Session hibSession)
			throws ConnectionLostException, Exception {
		
        ImageStore ist = server.components.lookup(ImageStore.class);

        if ( hibSession == null ) hibSession = createHibSession(server,caddy);

        DataRequest req = (DataRequest) request;
        final Map<String, String> map = req.getArguments();

        String filterName = map.get("filter");
        Long id = null;
        try {
            id = Long.parseLong(map.get("id"));
        } catch ( Exception e ) {
        }
        if ( id == null ) throw new MissingArgumentException(this,"id");

        ImageManager im = ist.createImageManager(hibSession);

        Asset asset = null;

        asset = im.getImage(id,filterName);

        if ( asset != null )
		    response.setContent( asset.getContent());
        else
		    response.setContent( PHPContent.NOT_FOUND );
		


	}

}
