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

import java.io.File;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hibernate.Session;
import org.jdom.Element;

import de.cosmocode.palava.ComponentException;
import de.cosmocode.palava.components.cstore.FSContentStore;
import de.cosmocode.palava.core.server.Server;

public class ImageStore extends AssetStore {

	File dir;
	private static final String PATTERN = "[a-zA-Z0-9]+";
	Pattern validFilterName = Pattern.compile(PATTERN);

	public File getFile( String storeKey, String filterName ){
		return new File(dir,filterName + "/" + storeKey);
	}
	
	public ImageManager createImageManager(Session session){
		return new ImageManager(this,store,session);
	}
    @Override
	public void configure(Element root, Server server)
			throws ComponentException {
	
		super.configure(root, server);

		
		// TODO test
		dir = new File(root.getChildText("filterdir"));
		
		store = new FSContentStore();
		store.configure(root.getChild("store"), server);
				
		@SuppressWarnings("unchecked")
		List<Element> children = root.getChild("filters").getChildren("filter");
		
		for ( Element filter : children ) {
			String name = filter.getAttributeValue("name");
			
			// test name
			
			Matcher m = validFilterName.matcher(name);
			if (!m.matches()) {
			    throw new ComponentException(
		            "bad filter name " + name + " : should match " + validFilterName.pattern());
			}
			
		}
	}
	
	@Override
	public void initialize() {
		super.initialize();
		if ( ! dir.exists() )
			dir.mkdirs();
	}


}
