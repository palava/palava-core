package de.cosmocode.palava;

import java.io.OutputStream;


/**
 * abstract, implements mimetype and length
 * @author Detlef Hüttemann
 */
public abstract class Content
{
    MimeType _mime;
    long _length;
    long _lastMod;

    public void setMimeType( MimeType mime ) {
        _mime = mime;
    }
    public MimeType getMimeType() {
        return _mime;
    }
    public long getLength() {
        return _length;
    }
    public void setLength( long length ) {
    	this._length = length;
    }


    public abstract void write ( OutputStream out ) throws Exception ;
    
}
