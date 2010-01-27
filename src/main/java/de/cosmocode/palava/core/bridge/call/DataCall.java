package de.cosmocode.palava.core.bridge.call;

import java.util.Map;


public interface DataCall extends Call {
 
    Map<String, String> getStringedArguments();
    
}
