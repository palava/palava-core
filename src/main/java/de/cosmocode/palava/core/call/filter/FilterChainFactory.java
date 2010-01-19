package de.cosmocode.palava.core.call.filter;

public interface FilterChainFactory {

    public FilterChain create(FilterChain proceeding);
    
}
