package de.cosmocode.palava;


/**
 * a mimetype and some static mimetypes
 * @author Detlef Hüttemann
 */
public class MimeType
{
    private String _type;
    private int _slash;

	public static final MimeType Error = new MimeType("application/error");
    public static final MimeType PHP = new MimeType("application/x-httpd-php");
    public static final MimeType JSON = new MimeType("application/json");
	public static final MimeType Text = new MimeType("text/plain");
	public static final MimeType XML = new MimeType("application/xml");
	public static final MimeType HTML = new MimeType("text/html");
	public static final MimeType Image = new MimeType("image/*");
	public static final MimeType Jpeg = new MimeType("image/jpeg");
	

    public MimeType( String type ) {
        if ( type == null ) throw new NullPointerException("MimeType");
        _type = type;
        _slash = _type.indexOf("/");
        if ( _slash <0 || _slash == _type.length()-1 )
        	throw new IllegalArgumentException("missing '/' in mimetype declaration");
    }

    public String toString() {
        return _type;
    }
    public boolean equals( Object o ) {
        return (o instanceof MimeType) && _type.equals(((MimeType)o)._type);
    }

    /** returns the part after the '/'
     */
	public String getMinor() {
		
		return _type.substring(_slash+1);
	}

}
