package de.cosmocode.palava.core.command.filter;

/**
 * Indicating an error during filter execution.
 *
 * @author Willi Schoenborn
 */
public class FilterException extends Exception {

    private static final long serialVersionUID = -632863612246760392L;

    public FilterException(String message) {
        super(message);
    }
    
    public FilterException(Throwable throwable) {
        super(throwable);
    }
    
    public FilterException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
