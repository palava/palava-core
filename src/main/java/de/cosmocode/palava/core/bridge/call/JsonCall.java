package de.cosmocode.palava.core.bridge.call;

import org.json.JSONException;
import org.json.JSONObject;

import de.cosmocode.palava.core.bridge.simple.ConnectionLostException;

public interface JsonCall extends Call {
    
    JSONObject getJSONObject() throws ConnectionLostException, JSONException;
    
}
