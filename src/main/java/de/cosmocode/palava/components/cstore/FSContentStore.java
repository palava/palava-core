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

package de.cosmocode.palava.components.cstore;

import java.io.File;
import java.io.FileOutputStream;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import de.cosmocode.palava.MimeType;
import de.cosmocode.palava.core.protocol.content.Content;
import de.cosmocode.palava.core.protocol.content.FileContent;
import de.cosmocode.palava.core.protocol.content.StreamContent;

/**
 * 
 *
 * @author Willi Schoenborn
 */
public class FSContentStore implements ContentStore {
    
    private static final Logger log = Logger.getLogger(FSContentStore.class);
    
    private File dir;
    
    @Inject
    public FSContentStore(@Named("content.store.root") String root) {
        dir = new File(root);
    }

    private String generateFilename(MimeType mimeType) {
        return DigestUtils.md5Hex(Long.toString(System.currentTimeMillis())) + "." + mimeType.getMinor();
    }

    private File mkFile(String name) {
        return new File(dir, name);
    }

    @Override
    public StreamContent load(String key) throws Exception {
        final File file = mkFile(key);

        if (!file.exists()) return null;
        return new FileContent(file);
    }
    
    @Override
    public String store(Content content) throws Exception {
        
        final String name = generateFilename(content.getMimeType());
        final File file = mkFile(name);
        
        final FileOutputStream out = new FileOutputStream(file);
        content.write(out);
        out.flush();
        out.close();
        return name;
    }

//    public String store(InputStream in, MimeType mimeType) throws Exception {
//        final String name = generateFilename(mimeType);
//        final File file = mkFile(name);
//        
//        final FileOutputStream out = new FileOutputStream(file);
//        
//        IOUtils.copy(in, out);
//        out.flush();
//        out.close();
//        
//        return name;
//    }

    @Override
    public void remove(String key) {
        final File file = mkFile(key);
        if (!file.delete()) log.error("cannot delete " + file.getAbsolutePath());
    }

}
