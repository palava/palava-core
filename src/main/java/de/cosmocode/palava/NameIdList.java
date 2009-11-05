package de.cosmocode.palava;

import java.util.ArrayList;
import java.util.List;

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