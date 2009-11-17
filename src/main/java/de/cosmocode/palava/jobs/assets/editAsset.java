/**
 * palava - a java-php-bridge
 * Copyright (C) 2007  CosmoCode GmbH
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

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