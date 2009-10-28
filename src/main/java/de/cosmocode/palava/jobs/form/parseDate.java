package de.cosmocode.palava.jobs.form;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.json.JSONStringer;
import org.json.extension.JSONConstructor;

import de.cosmocode.palava.ConnectionLostException;
import de.cosmocode.palava.JSONContent;
import de.cosmocode.palava.Response;
import de.cosmocode.palava.Server;
import de.cosmocode.palava.Session;
import de.cosmocode.palava.jobs.CachableJSONJob;

public class parseDate extends CachableJSONJob {

    private static final Logger log = Logger.getLogger(parseDate.class); 
    
    @Override
    protected void process(JSONObject json, Response response, Session session, Server server, 
        Map<String, Object> caddy) throws ConnectionLostException, Exception {

        require("pattern", "source");
        
        final String pattern = json.getString("pattern");
        final String source = json.getString("source");
        
        final DateFormat dateFormat = new SimpleDateFormat(pattern);
        dateFormat.setLenient(false);
        
        final JSONConstructor out = new JSONStringer();

        Object returnValue = Boolean.FALSE;
        
        try {
            final Date date = dateFormat.parse(source);
            returnValue = date.getTime() / 1000;
        } catch (ParseException e) {
            log.info("Parsing date " + source + " with pattern " + pattern + " failed.");
        }

        out.object().
            key("date").value(returnValue).
        endObject();
        
        response.setContent(new JSONContent(out));
        
    }

}
