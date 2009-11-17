package de.cosmocode.palava;

import java.util.Map;


/**
 * useragent and ip of the browser
 * @author Detlef Hüttemann
 */
public class ClientData
{
    private String _ua;
    private String _ip;

    public static final String UA = "ua";
    public static final String IP = "ip";

    public ClientData( String ua, String ip ) {
        _ua = ua;
        _ip = ip;
    }
    public ClientData( Map<String,String> map ) {
        this( map.get( UA ), map.get( IP ) ) ;
    }

    public boolean isValid( Map<String,String> map ) {
        return equals( _ip , map.get( IP ) );        
    }
    
    public String getIP() {
        return _ip;
    }
    
    public String getUserAgent() {
        return _ua;
    }

    private static boolean equals ( String s1, String s2 ) {
        return s1 != null ? s1.equals(s2) : s2 == null;
    }
    
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof ClientData)) {
            return false;
        }
        ClientData that = (ClientData) object;
        return that.getIP().equals(this.getIP()) && that.getUserAgent().equals(this.getUserAgent());
    }
}
