package de.cosmocode.palava.jobs.system;

import java.io.StringWriter;
import java.util.Map;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import de.cosmocode.palava.Client;
import de.cosmocode.palava.ConnectionLostException;
import de.cosmocode.palava.Job;
import de.cosmocode.palava.Request;
import de.cosmocode.palava.Response;
import de.cosmocode.palava.Server;
import de.cosmocode.palava.Session;
import de.cosmocode.palava.TextContent;
import de.cosmocode.palava.TextRequest;


/**
 * offers a javascript enviroment in the server
 * @author Tobias Sarnowski
 */
public class console implements Job
{

	public void process(Request request, Response response, Session session, Server server, Map<String,Object> caddy) throws ConnectionLostException, Exception
	{
		// get the code
		String jscode = ((TextRequest)request).getText();

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

		Object jsClient = Context.javaToJS(new Client(), scope);
		ScriptableObject.putProperty(scope, "Client", jsClient);

		Object jsServer = Context.javaToJS(server, scope);
		ScriptableObject.putProperty(scope, "server", jsServer);

		// prepare the code
		Script script = context.compileString(jscode, "console", 1, null);

		// execute it!
		// FIXME use a real ScriptReturnObject to transmit the results
		//       this is also ugly, let php or the binclient do the formating
		try {
			Object returned = script.exec(context, scope);
			if (returned != null && returned.toString().length() > 0 && ! (returned instanceof org.mozilla.javascript.Undefined))
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
		if (!response.contentSet())
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
