package de.cosmocode.palava;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.log4j.Logger;


/**
 * use the PHPConverter to produce php output of java objects
 * @author Detlef Hüttemann
 */
@Deprecated
public class PHPContent extends Content
{
    private static final Logger logger = Logger.getLogger( PHPContent.class ) ;

    public static PHPContent OK = null;
    public static PHPContent NOT_FOUND = null;
    
    static {
        try {
            OK = new PHPContent("ok") ;
            NOT_FOUND = new PHPContent("not_found") ;
        } catch (ConversionException e ) {
            logger.error("cannot create default objects", e );
        }
    };

    byte [] _bytes;
    public PHPContent( Object object ) throws ConversionException {
        PHPConverter converter = new PHPConverter () ;
        StringBuffer buf = new StringBuffer () ;
        converter.convert( buf, object ) ;
        _bytes = buf.toString().getBytes();
        _length = _bytes.length;
        _mime = MimeType.PHP;
    }
    public void write( OutputStream out ) throws IOException {
        out.write( _bytes, 0, (int)_length );
    }
}


