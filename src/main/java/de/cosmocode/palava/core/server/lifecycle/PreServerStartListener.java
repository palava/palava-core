package de.cosmocode.palava.core.server.lifecycle;

import de.cosmocode.palava.core.registry.Procedure;

public interface PreServerStartListener {

    public static final Procedure<PreServerStartListener> COMMAND = new Procedure<PreServerStartListener>() {
        
        @Override
        public void apply(PreServerStartListener listener) {
            listener.beforeStart();
        }
        
    };

    void beforeStart();
    
}
