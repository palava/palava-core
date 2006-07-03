package de.cosmocode.palava;

public interface JobInterceptor {

    void intercept(ComponentManager manager, Job job);
    
}
