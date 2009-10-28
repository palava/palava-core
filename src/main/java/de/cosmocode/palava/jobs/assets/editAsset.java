package de.cosmocode.palava.jobs.assets;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import de.cosmocode.palava.DataRequest;
import de.cosmocode.palava.MissingArgumentException;
import de.cosmocode.palava.PHPContent;
import de.cosmocode.palava.Request;
import de.cosmocode.palava.Response;
import de.cosmocode.palava.Server;
import de.cosmocode.palava.Session;
import de.cosmocode.palava.UpdateResult;
import de.cosmocode.palava.components.assets.Asset;
import de.cosmocode.palava.components.assets.ImageManager;
import de.cosmocode.palava.components.assets.ImageStore;
import de.cosmocode.palava.jobs.hib.HibJob;

public class editAsset extends HibJob {

	@SuppressWarnings("unchecked")
	@Override
	public void process(Request req, Response resp, Session session,
			Server server, Map<String, Object> caddy,
			org.hibernate.Session hibSession) throws Exception {
		
        ImageStore as = server.components.lookup(ImageStore.class);
        if ( hibSession == null ) hibSession = createHibSession(server,caddy);

        DataRequest request = (DataRequest) req;
        Map<String,String> map = request.getArgs();

        String assetId = map.get("assetId");
        if ( assetId == null ) throw new MissingArgumentException(this,"assetId");
        map.remove("assetId");

        String title = map.get("title");
        if ( title == null ) throw new MissingArgumentException(this,"title");
        map.remove("title");

        String description = map.get("description");
        if ( description == null ) throw new MissingArgumentException(this,"description");
        map.remove("description");
        
        String exDate = map.get("expirationDate"); 
        map.remove("expirationDate");

        ImageManager am = as.createImageManager(hibSession);

        UpdateResult ur = new UpdateResult();
        try {
            Asset asset = am.getAsset( Long.parseLong(assetId), false ) ;

            if ( asset == null ) throw new NullPointerException("asset") ;

            asset.setTitle( title ) ;
            asset.setDescription( description );
                   
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
            Date date = null;
            
    		try {
                format.setLenient(false);
                if (!exDate.equals("")) date = format.parse(exDate);
    		} catch (Exception e) {
    			ur.addError(UpdateResult.ERR_FORMAT, "expirationDate");
    		}
    	
        	asset.setExpirationDate(date);
    		asset.setExpiresNever(map.containsKey("expiresNever") && map.get("expiresNever").equals("true"));
    		map.remove("expiresNever");
    		
    		asset.fillMetaData(map);

    		if (ur.isError()) {
    			ur.setResult(asset);
    			resp.setContent(new PHPContent(ur));
    			return;
    		}
    		
            am.updateAsset(asset);
            ur.setResult(asset);
        } catch ( Exception e ) {
            ur.setException(e);
        }
		resp.setContent(new PHPContent(ur));
	}
}