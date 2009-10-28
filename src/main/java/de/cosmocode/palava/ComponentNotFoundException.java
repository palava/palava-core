package de.cosmocode.palava;

public class ComponentNotFoundException extends RuntimeException {

    private static final long serialVersionUID = -5631423543797268353L;

    public ComponentNotFoundException(String name) {
        super(name);
    }
    
}