package de.cosmocode.palava;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * parse the request content as a json object
 * @author Detlef Huettemann
 */
public class JSONRequest extends Request
{
    private String text = null;
    private JSONObject json = null;


	public JSONRequest(RequestHeader header, InputStream in)
	{
		super(header, in);
	}


	public String getText() throws ConnectionLostException, IOException
	{
		if (text == null)
		{
			byte[] buffer = new byte[(int)header.getContentLength()];
			read(buffer);
			ByteBuffer bb = ByteBuffer.wrap(buffer);
			CharBuffer cb = _charset.decode(bb) ;
			text = cb.toString();
		}
		return text;
	}
	public JSONObject getJSONObject() throws ConnectionLostException, IOException, JSONException {
        if ( json == null ) {
            json = new JSONObject(getText());
        }
        return json;
    }

}
