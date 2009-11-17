/**
 * palava - a java-php-bridge
 * Copyright (C) 2007  CosmoCode GmbH
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

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
