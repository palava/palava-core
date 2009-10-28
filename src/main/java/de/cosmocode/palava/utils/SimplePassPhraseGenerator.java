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
