package de.cosmocode.palava.jobs.captcha;

import java.io.ByteArrayInputStream;
import java.util.Map;

import de.cosmocode.palava.ConnectionLostException;
import de.cosmocode.palava.Job;
import de.cosmocode.palava.MimeType;
import de.cosmocode.palava.Request;
import de.cosmocode.palava.Response;
import de.cosmocode.palava.Server;
import de.cosmocode.palava.Session;
import de.cosmocode.palava.StreamContent;
import de.cosmocode.palava.components.captcha.Captcha;

public class createCaptcha implements Job {

	@Override
	public void process(Request request, Response response, Session session,
			Server server, Map<String, Object> caddy)
			throws ConnectionLostException, Exception {

		Captcha captcha = server.components.lookup(Captcha.class);
		
		byte [] bytes = captcha.getJpegCapchta(session.getSessionID());
		
		ByteArrayInputStream in = new ByteArrayInputStream(bytes);
		StreamContent content = new StreamContent(in, bytes.length, MimeType.Jpeg);
		
		response.setContent( content );
	}

}
