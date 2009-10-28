package de.cosmocode.palava.jobs.cache;

import java.util.Map;

import de.cosmocode.palava.CachableJob;
import de.cosmocode.palava.ConnectionLostException;
import de.cosmocode.palava.Job;
import de.cosmocode.palava.Request;
import de.cosmocode.palava.Response;
import de.cosmocode.palava.Server;
import de.cosmocode.palava.Session;
import de.cosmocode.palava.TextContent;

public class clear implements Job {

    public void process(Request request, Response response, Session session,
            Server server, Map<String, Object> caddy)
            throws ConnectionLostException, Exception {
        
        response.setContent(new TextContent("Items removed from cache: " + CachableJob.clearCache()));

    }

}
