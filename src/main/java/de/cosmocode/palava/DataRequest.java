package de.cosmocode.palava;

import java.io.IOException;
import java.io.InputStream;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;


/**
 * parses the content of a datarequest into a map
 * @author Detlef Hüttemann
 */
public class DataRequest extends Request
{
	private static Logger logger = Logger.getLogger( DataRequest.class ) ;


    private Map<String,String> _args;


	public DataRequest(RequestHeader header, InputStream in)
	{
		super(header, in);
	}

    @Deprecated
    public <K, V> Map<K, V> getArgs() throws ConnectionLostException, IOException {
        if (_args == null) parseArgs();

        @SuppressWarnings("unchecked")
        final Map<K, V> args = (Map<K, V>) _args;
        
        return args;
    }

    public Map<String, String> getArguments() throws ConnectionLostException, IOException {
        if (_args == null) parseArgs();
        return _args;
    }

    private void parseArgs () throws ConnectionLostException, IOException
	{
        if ( _args != null ) throw new IllegalStateException("args already present");

        _args = new HashMap<String,String>();

		// FIXME datenrequest mit einem datensatz von > sizeof(int) gehen verloren!
        byte[] buffer = new byte[(int)header.getContentLength()];

        read(buffer);

        ByteBuffer bb = ByteBuffer.wrap( buffer ) ;
        CharBuffer cb = _charset.decode( bb ) ;

        if ( logger.isDebugEnabled() ) 
            logger.debug(this.header.getJob() + " - Got args:  " + cb.toString() ) ;

        boolean finished = false;
        boolean escaped = false;
        StringBuffer name = new StringBuffer(), value = null;
        while ( !finished ) {
            try {
                char c = cb.get();
                switch ( c ) {
                case '=':
			if (!escaped) {
	                    value = new StringBuffer();
        	            break;
			}
                case '&':
			if (!escaped) {
	                    if ( value != null ) {
        	                _args.put( name.toString(), value.toString() );
                	    }
	                    value = null;
        	            name = new StringBuffer();
                	    break;
			}
                case '\\':
                    if (!escaped) {
                        escaped = true;
			break;
                    } else {
		    	escaped = false;
		    }
                default:
                    if ( value != null ) 
                        value.append(c);
                    else
                        name.append(c);
                }
            } catch ( BufferUnderflowException e ) {
                finished = true;
            }
        }
        if ( value != null )
            _args.put( name.toString(), value.toString() );
    }

}
