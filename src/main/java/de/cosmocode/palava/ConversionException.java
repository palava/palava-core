package de.cosmocode.palava;


/**
 * used by the converter to indicate conversion problems
 * @author Detlef Hüttemann
 */
public class ConversionException extends Exception {

    private static final long serialVersionUID = 2345955837144390619L;

    public ConversionException( String msg ) {
        super( msg ) ;
    }
}
