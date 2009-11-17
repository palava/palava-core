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
