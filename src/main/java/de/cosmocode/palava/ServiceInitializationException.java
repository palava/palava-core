package de.cosmocode.palava;

/**
 * Indicates an error durint {@link Service#initialize()}.
 *
 * @author Willi Schoenborn
 */
public final class ServiceInitializationException extends Exception {

    private static final long serialVersionUID = 3953941499290200392L;

    public ServiceInitializationException(String message) {
        super(message);
    }
    
    public ServiceInitializationException(Throwable throwable) {
        super(throwable);
    }
    
    public ServiceInitializationException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
