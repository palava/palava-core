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

import java.util.ArrayList;
import java.util.List;

/**
 * 
 *
 * @deprecated use Map instead
 */
@Deprecated
public class NameIdList implements Convertible {

	public static class NameId {
		String id;
		String name;
		NameId(String id, String name){
			this.id = id;
			this.name = name;
		}
	}
	
	public NameIdList() {
		this.list = new ArrayList<NameId>();
		
	}
	private List<NameId> list = null;
	
	public void add( String name, String id ){
		list.add( new NameId(id,name));	
	}
	@Override
	public void convert(StringBuffer buf, ContentConverter converter)
			throws ConversionException {
		
		int size = list.size();
		
		if ( size == 0)
			converter.convertKeyValue(buf,null,null,KeyValueState.ZERO);
		else {
			NameId ni = list.get(0);
			if ( size == 1 ) 
				converter.convertKeyValue(buf,ni.id,ni.name, KeyValueState.SINGLE);
			else {		
				converter.convertKeyValue(buf,ni.id,ni.name, KeyValueState.START);
				for ( int i=1;i<size-1;i++){
					ni = list.get(i);
					
					converter.convertKeyValue(buf,ni.id,ni.name, KeyValueState.INSIDE);
				}
				ni = list.get(size-1);
				converter.convertKeyValue(buf,ni.id,ni.name, KeyValueState.LAST);
			}
	
		}

	}

}
