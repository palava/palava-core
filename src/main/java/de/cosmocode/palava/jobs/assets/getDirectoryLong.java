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

import java.util.List;
import java.util.Map;

import org.json.JSONStringer;
import org.json.extension.JSONConstructor;

import de.cosmocode.palava.MissingArgumentException;
import de.cosmocode.palava.Server;
import de.cosmocode.palava.components.assets.Asset;
import de.cosmocode.palava.components.assets.Directory;
import de.cosmocode.palava.components.assets.ImageManager;
import de.cosmocode.palava.components.assets.ImageStore;
import de.cosmocode.palava.core.protocol.DataRequest;
import de.cosmocode.palava.core.protocol.JSONContent;
import de.cosmocode.palava.core.protocol.Call;
import de.cosmocode.palava.core.protocol.Response;
import de.cosmocode.palava.core.session.HttpSession;
import de.cosmocode.palava.jobs.hib.HibJob;

public class getDirectoryLong extends HibJob {

	@Override
	public void process(Call req, Response response, HttpSession session, Server server, 
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