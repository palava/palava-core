package de.cosmocode.palava;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.log4j.Logger;


/**
 * send the result as plain text
 * @author Detlef Hüttemann
 */
public class TextContent extends Content
{

	private static final Logger logger = Logger.getLogger( TextContent.class ) ;


    private byte [] _bytes;


    public TextContent( Object object ) throws ConversionException
	{
        TextConverter converter = new TextConverter () ;
        StringBuffer buf = new StringBuffer () ;
        converter.convert( buf, object ) ;
        _bytes = buf.toString().getBytes();
        _length = _bytes.length;
        _mime = MimeType.Text;
    }


    public void write( OutputStream out ) throws IOException {
        out.write( _bytes, 0, (int)_length );
    }
}


