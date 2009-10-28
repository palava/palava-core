package de.cosmocode.palava;

/** JSONPath - a representation of a (nested) JSON field
 * example: 
 * person
 * person.firstname
 * person[13].firstname
 * orga[30].person[13].firstname
 *
 */
public class JSONPath {
    char [] buf;

    public JSONPath( ) {
        buf = null;
    }
    public JSONPath( char [] buf ) {
        this.buf = buf;
    }
    public String toString() {
        return buf != null ? new String(buf) : "";
    }
    JSONPath( String str ) {
        buf = str.toCharArray();
    }
    public JSONPath field( String field ) {
    	
    	if ( buf != null ) {
    		char [] b = new char[buf.length+1+field.length()];

    		System.arraycopy(buf,0,b,0,buf.length);
    		b[buf.length] = '.';
    		System.arraycopy(field.toCharArray(),0,b,buf.length+1,field.length());

    		return new JSONPath( b );
    	} else {
    		char [] b = new char[field.length()];

    		System.arraycopy(field.toCharArray(),0,b,0,field.length());

    		return new JSONPath( b );
    		
    	}
    }
    public JSONPath index( int idx ) {
        String field = Integer.toString(idx);
        char [] b = new char[buf.length+2+field.length()];

        System.arraycopy(buf,0,b,0,buf.length);
        b[buf.length] = '[';
        System.arraycopy(field.toCharArray(),0,b,buf.length+1,field.length());
        b[b.length-1] = ']';

        return new JSONPath( b );
    }
    
}
