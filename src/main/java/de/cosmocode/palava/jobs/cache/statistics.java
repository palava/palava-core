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

package de.cosmocode.palava.jobs.cache;

import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.json.JSONStringer;
import org.json.extension.JSONConstructor;

import de.cosmocode.palava.CachableJob;
import de.cosmocode.palava.ConnectionLostException;
import de.cosmocode.palava.Job;
import de.cosmocode.palava.Server;
import de.cosmocode.palava.core.protocol.JSONContent;
import de.cosmocode.palava.core.protocol.Request;
import de.cosmocode.palava.core.protocol.Response;
import de.cosmocode.palava.core.session.HttpSession;

public class statistics implements Job {

    public void process(Request request, Response response, HttpSession session,
            Server server, Map<String, Object> caddy)
            throws ConnectionLostException, Exception {
        
        final String usedCache_h = FileUtils.byteCountToDisplaySize(CachableJob.getCurrentUsedCacheSize());
        final String maxCache_h = FileUtils.byteCountToDisplaySize(CachableJob.getMaxCacheSize());
        
        // build the final json code
        final JSONConstructor out = new JSONStringer();
        out.object().
            key("memory_size").object().
                key("used_h").value(usedCache_h).
                key("max_h").value(maxCache_h).
                key("used").value(CachableJob.getCurrentUsedCacheSize()).
                key("max").value(CachableJob.getMaxCacheSize()).
            endObject().
            key("java_heap_size").object().
                key("used").value(FileUtils.byteCountToDisplaySize(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())).
                key("free").value(FileUtils.byteCountToDisplaySize(Runtime.getRuntime().freeMemory())).
                key("total").value(FileUtils.byteCountToDisplaySize(Runtime.getRuntime().totalMemory())).
                key("max").value(FileUtils.byteCountToDisplaySize(Runtime.getRuntime().maxMemory())).
            endObject().
            key("items").value(CachableJob.getItemCount())
            ;
        out.endObject();
        response.setContent(new JSONContent(out));
    }

}