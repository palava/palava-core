/**
 * palava - a java-php-bridge
 * Copyright (C) 2007-2010  CosmoCode GmbH
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

package de.cosmocode.palava.jobs.system;

import java.io.StringWriter;
import java.util.Map;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.Undefined;

import de.cosmocode.palava.core.bridge.call.Call;
import de.cosmocode.palava.core.bridge.call.TextCall;
import de.cosmocode.palava.core.bridge.command.Response;
import de.cosmocode.palava.core.bridge.session.HttpSession;
import de.cosmocode.palava.core.bridge.simple.ConnectionLostException;
import de.cosmocode.palava.core.bridge.simple.content.TextContent;
import de.cosmocode.palava.legacy.Job;
import de.cosmocode.palava.legacy.server.Server;

/**
 * Offers a javascript enviroment in the server.
 * 
 * @author Tobias Sarnowski
 */
public class console implements Job
{

    public void process(Call request, Response response, HttpSession session, Server server, Map<String,Object> caddy) throws ConnectionLostException, Exception
    {
        // get the code
        String jscode = ((TextCall)request).getText();

        // initialize our world
        Context context = Context.enter();
        Scriptable scope = context.initStandardObjects();

        // give him all our important objects
        StringWriter sout = new StringWriter();
        Object jsOut = Context.javaToJS(sout, scope);
        ScriptableObject.putProperty(scope, "out", jsOut);

        Object jsRequest = Context.javaToJS(request, scope);
        ScriptableObject.putProperty(scope, "request", jsRequest);

        Object jsResponse = Context.javaToJS(response, scope);
        ScriptableObject.putProperty(scope, "response", jsResponse);

        Object jsSession = Context.javaToJS(session, scope);
        ScriptableObject.putProperty(scope, "session", jsSession);

        Object jsServer = Context.javaToJS(server, scope);
        ScriptableObject.putProperty(scope, "server", jsServer);

        // prepare the code
        Script script = context.compileString(jscode, "console", 1, null);

        // execute it!
        // FIXME use a real ScriptReturnObject to transmit the results
        //       this is also ugly, let php or the binclient do the formating
        try {
            Object returned = script.exec(context, scope);
            if (returned != null && returned.toString().length() > 0 && ! (returned instanceof Undefined))
                sout.write("\n%%GREEN+%%" + returned.toString() + "%%+GREEN%%");
            sout.write("\n%%GREY+%%Script successful executed.%%+GREY%%");
        } catch (Exception e) {
            String msg = e.getMessage();
            if (msg == null)
                msg = e.toString();
            sout.write("\n%%RED+%%" + htmlspecialchars(msg) + "%%+RED%%");
        }
        finally {
            context.exit();
        }

        // send the output
        if (!response.hasContent())
            response.setContent(new TextContent(sout.toString()));
    }

    private String htmlspecialchars(String text)
    {
        text = str_replace(text, "<", "&lt;");
        text = str_replace(text, ">", "&gt;");

        return text;
    }

    private String str_replace(String text, String search, String replace)
    {
        int pos = text.indexOf(search);
        while (pos >= 0) {
            String start = text.substring(0, pos);
            String end = text.substring(pos + search.length());
            text = start + replace + end;
            pos = text.indexOf(search);
        }

        return text;
    }
}
