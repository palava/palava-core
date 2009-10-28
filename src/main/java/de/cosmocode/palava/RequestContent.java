package de.cosmocode.palava;

/** a content based an request data.
 * 
 * @author huettemann
 *
 */
public class RequestContent extends StreamContent {

	public RequestContent(Request request, MimeType mimeType)
			throws Exception {
		super(request.getInputStream(), request.header.getContentLength(), mimeType);
	}

}
