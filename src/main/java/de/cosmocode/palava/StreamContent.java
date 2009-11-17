package de.cosmocode.palava;

import java.io.InputStream;
import java.io.OutputStream;


/**
 * use this for all stream responses
 * @author Tobias Sarnowski
 */
public class StreamContent extends Content
{
    private static final int BUFSIZE = 1024;

	private InputStream in;
	
	public StreamContent(InputStream in, long length, MimeType mime) throws Exception
	{
		this.in = in;
		_length = length;
		_mime = mime;
	}
	
	/** returns the underlying inputstream
	 * 
	 * note that if you read from the input stream manually, 
	 * the write StreamContent.write method will fail
	 *
	 * @return InputStream
	 */
	public InputStream getInputStream() {
		return in;
	}


	/**
	 * copy the underlying inputstream to the given output stream
	 * this method can be called only once.
	 */
	public void write(OutputStream out) throws Exception
	{
		_copy(in,out);
	}
	
	public static void copy(InputStream in, OutputStream out)throws Exception {
        byte [] buf = new byte[ BUFSIZE ];
        int done;

        while (0 < (done = in.read(buf, 0, BUFSIZE))) {
			out.write(buf, 0, done);

	}

        in.close();
		
	}
	public void _copy(InputStream in, OutputStream out)throws Exception {

	int bufs = BUFSIZE;
	if (_length < bufs) bufs = (int)_length;

        byte [] buf = new byte[ bufs ];
        int done;
        int read=0;
        while (0 < (done = in.read(buf, 0, buf.length))) {

			out.write(buf, 0, done);
	            if ( _length <= (read += done)) break;

		if (buf.length > (int)(_length - read)) {
			buf = new byte[(int)(_length - read)];
		}
        }

        in.close();
	}

	public void setInputStream(InputStream in) {
		this.in = in;
		
	}
}
