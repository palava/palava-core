package de.cosmocode.palava.jobs.cache;

import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.json.JSONStringer;
import org.json.extension.JSONConstructor;

import de.cosmocode.palava.CachableJob;
import de.cosmocode.palava.ConnectionLostException;
import de.cosmocode.palava.JSONContent;
import de.cosmocode.palava.Job;
import de.cosmocode.palava.Request;
import de.cosmocode.palava.Response;
import de.cosmocode.palava.Server;
import de.cosmocode.palava.Session;

public class statistics implements Job {

    public void process(Request request, Response response, Session session,
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