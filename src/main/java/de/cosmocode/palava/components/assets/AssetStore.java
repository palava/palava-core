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

package de.cosmocode.palava.components.assets;

import org.hibernate.Session;
import org.jdom.Element;

import de.cosmocode.palava.Component;
import de.cosmocode.palava.ComponentException;
import de.cosmocode.palava.ComponentManager;
import de.cosmocode.palava.components.cstore.FSContentStore;
import de.cosmocode.palava.core.server.Server;

public class AssetStore implements Component {

	FSContentStore store;
	
	public AssetManager createAssetManager(Session session) {
		return new AssetManager(store,session);
	}

	@Override
	public void compose(ComponentManager manager) throws ComponentException {
		store.compose(manager);
	}

	@Override
	public void configure(Element root, Server server)
			throws ComponentException {
	
		store = new FSContentStore();
		store.configure(root.getChild("store"), server);
	}

	@Override
	public void initialize() throws ComponentException {
		try {
            store.initialize();
        } catch (Exception e) {
            throw new ComponentException(e);
        }
	}
	


}
