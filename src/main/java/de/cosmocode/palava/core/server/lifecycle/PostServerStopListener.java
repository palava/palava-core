package de.cosmocode.palava.core.server.lifecycle;

import de.cosmocode.palava.core.registry.Procedure;

public interface PostServerStopListener {

    public static final Procedure<PostServerStopListener> COMMAND = new Procedure<PostServerStopListener>() {
        
        @Override
        public void apply(PostServerStopListener listener) {
            listener.afterStop();
        }
        
    };

    void afterStop();
    
}
