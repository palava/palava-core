package de.cosmocode.palava.jobs.assets;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import de.cosmocode.palava.ConnectionLostException;
import de.cosmocode.palava.DataRequest;
import de.cosmocode.palava.Job;
import de.cosmocode.palava.MimeType;
import de.cosmocode.palava.MissingArgumentException;
import de.cosmocode.palava.PHPContent;
import de.cosmocode.palava.Request;
import de.cosmocode.palava.Response;
import de.cosmocode.palava.Server;
import de.cosmocode.palava.Session;
import de.cosmocode.palava.components.assets.Asset;

public class upload0 implements Job {

	@Override
	public void process(Request request, Response response, Session session,
			Server server, Map<String, Object> caddy) throws ConnectionLostException, Exception {
		
        DataRequest req = (DataRequest) request;
        final Map<String, String> map = req.getArguments();

        String mimetype = map.get("mimetype");
        if ( mimetype == null ) throw new MissingArgumentException(this,"mimetype");

        Asset asset = new Asset();
        asset.setName( map.remove("name") );
        asset.setDescription(map.remove("description"));
        asset.setTitle(map.remove("title"));
        asset.setExpiresNever(map.containsKey("expires"));
        map.remove("expires");
        
        String exDate = map.remove("expirationDate");        
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        Date date = null;
        
		try {
			date = format.parse(exDate);
		} catch (Exception e) {
			//TODO handle format exception
		}
		
        asset.setExpirationDate(date);
        asset.fillMetaData(map);
                
        caddy.put("asset", asset );
        caddy.put("mimetype", new MimeType(mimetype) );

		response.setContent( PHPContent.OK );
	}

}
