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
package de.cosmocode.palava.utils;

import java.util.Random;

import org.jdom.Element;

import de.cosmocode.palava.Component;
import de.cosmocode.palava.ComponentException;
import de.cosmocode.palava.ComponentManager;
import de.cosmocode.palava.Server;

public class SimplePassPhraseGenerator implements PassPhraseGenerator, Component {

	  protected int length;
	  protected java.util.Random rand;

	  // thanks for inspirations by Ian F. Darwin, http://www.darwinsys.com/
	  protected static char[] chars = { 
		  'a', 'b', 'c', 'd', 'e', 'f', 'g','h', 'j', 'k', 'm', 'n', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w','x', 'y', 'z',
		  'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K','M', 'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
	      '2', '3', '4', '5', '6', '7', '8', '9', };

	  public SimplePassPhraseGenerator() {
		super();
	}


	@Override
	public String generatePassPhrase() {
		StringBuffer buf = new StringBuffer();
	    for (int i = 0; i < length; i++) {
	    	char next = chars[rand.nextInt(chars.length)];
	    	buf.append(next);
	    }
	    return buf.toString();
	}


	@Override
	public void compose(ComponentManager manager) throws ComponentException {
		
	}

	@Override
	public void configure(Element root, Server server)
			throws ComponentException {
		length = Integer.parseInt(root.getChildText("length"));	
	}


	@Override
	public void initialize() throws Exception {
		rand = new Random();
	}
}
