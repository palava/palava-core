package de.cosmocode.palava;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;


/**
 * can parse the mimetype from a file extension
 * @author Detlef Hüttemann
 */
public class MimeTypes
{

	public static MimeTypes SINGLETON;
    
    static {
        try {
            SINGLETON = new MimeTypes("/etc/mime.types");
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    };

	private Map<String,MimeType> _mimes;

    private MimeTypes( String file ) throws Exception {
        _mimes = new HashMap<String,MimeType>();
        BufferedReader reader = new BufferedReader( new FileReader( file ) ) ;
        String line;
        Pattern pattern = Pattern.compile("\\s+");


        while ( null != ( line = reader.readLine())) {
            if ( line.length() > 0 && line.charAt(0) != '#' ) {
                String [] result = pattern.split( line ) ;
                if ( result.length > 1 ) {
                    MimeType mime = new MimeType(result[0]);
                    for ( int i=1;i<result.length;i++) {
                        _mimes.put( result[i], mime );
                    }
                }
            }
        }
    }

	public MimeType getMimeTypeByExt( String ext ) {
		return _mimes.get( ext ) ;
	}
	public MimeType getMimeTypeByName( String name ) {
        int dot = name.lastIndexOf(".");
        if ( dot != -1 ) 
		    return getMimeTypeByExt( name.substring(dot+1).toLowerCase());
        return null;
	}

}
