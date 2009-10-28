package de.cosmocode.palava;

public class UncachableException extends Exception {
    
    private static final long serialVersionUID = -4123365865843473019L;

    public UncachableException() {
    }

    public UncachableException(String message) {
        super(message);
    }

    public UncachableException(Throwable cause) {
        super(cause);
    }

    public UncachableException(String message, Throwable cause) {
        super(message, cause);
    }

}
