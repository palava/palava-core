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

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.cosmocode.palava.FileContent;
import de.cosmocode.palava.StreamContent;
import de.cosmocode.palava.components.cstore.ContentStore;

public class ImageManager extends AssetManager {
	
    private static final Logger log = LoggerFactory.getLogger(ImageManager.class);
    
	private final ImageStore imst;
	
	public ImageManager(ImageStore imst, ContentStore store, Session session) {
		super(store, session);
		this.imst = imst;
	}
	
	/** returns the (image) asset with content.
     * content is filtered according to the filtername
     * if the filterName is null, the original image will be returned.
     * 
     * @param id
     * @param filterName - may be null.
     * @return
     * @throws Exception 
     */
	public Asset getImage(Asset asset, String filterName) throws Exception {
	    
	    if (asset == null) throw new NullPointerException("asset must not be null");

	    if (asset.getContent() == null) {
	        loadAssetContent(asset);
	    }

        if ( filterName == null ) return asset;

        StreamContent content = asset.getContent();
    
        File filteredFile = imst.getFile(asset.getStoreKey(),filterName);
        
        log.info("filteredFile=" + filteredFile);
        
        if ( !filteredFile.exists() || asset.getModificationDate().getTime() > filteredFile.lastModified()){
            final String command = filteredFile.getParentFile() + "/convert.sh " + asset.getStoreKey();
            log.debug("Running: " + command);
            final Process proc = Runtime.getRuntime().exec(command);
            try {
                int exitValue = proc.waitFor();
                if (exitValue != 0)
                    log.error("Error during thumbnail generation. Error code: {}", exitValue);
            } catch (InterruptedException ie) {
                log.error("Thumbnail generation interrupted", ie);
                return null;
            }
            
            proc.getInputStream().close();
            proc.getOutputStream().close();
            proc.getErrorStream().close();
            
        }
        
        final FileContent filteredContent = new FileContent(filteredFile);
        filteredContent.setMimeType( content.getMimeType());
        asset.setContent( filteredContent);
        
        return asset;
	}

	/** returns the (image) asset with content.
	 * content is filtered according to the filtername
     * if the filterName is null, the original image will be returned.
	 * 
	 * @param id
	 * @param filterName - may be null.
	 * @return
	 * @throws Exception 
	 */
	public Asset getImage(Long id, String filterName) throws Exception {
	    final Asset asset = getAsset(id, true);

        if (asset == null ) {
            log.error("error: asset not found {}", id);
            return null;
        }
	    
		return getImage(asset, filterName);
	}
    
}