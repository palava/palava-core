package de.cosmocode.palava.components.mail;

public class EntityEncoder {

    private static final EntityEncoder instance = new EntityEncoder();
    
    private EntityEncoder() {
        
    }
    
    public String encode(String sourceString) {
        return sourceString.
            replace("&",  "&amp;").
            replace(">",  "&gt;").
            replace("<",  "&lt;").
            replace("'",  "&apos;").
            replace("\"", "&quot;").
            replace("%",  "&#37;");
    }

    public static EntityEncoder getInstance() {
        return instance; 
    }
    
}