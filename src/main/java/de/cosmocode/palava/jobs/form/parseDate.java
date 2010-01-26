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

import de.cosmocode.json.JSON;
import de.cosmocode.palava.core.command.Response;
import de.cosmocode.palava.core.protocol.ConnectionLostException;
import de.cosmocode.palava.core.protocol.content.JsonContent;
import de.cosmocode.palava.core.server.Server;
import de.cosmocode.palava.core.session.HttpSession;
import de.cosmocode.palava.legacy.CachableJSONJob;

public class parseDate extends CachableJSONJob {

    private static final Logger log = Logger.getLogger(parseDate.class); 
    
    @Override
    protected void process(JSONObject json, Response response, HttpSession session, Server server, 
        Map<String, Object> caddy) throws ConnectionLostException, Exception {

        require("pattern", "source");
        
        final String pattern = json.getString("pattern");
        final String source = json.getString("source");
        
        final DateFormat dateFormat = new SimpleDateFormat(pattern);
        dateFormat.setLenient(false);
        
        final JSONConstructor out = JSON.asJSONConstructor(new JSONStringer());

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
        
        response.setContent(new JsonContent(out));
        
    }

}
