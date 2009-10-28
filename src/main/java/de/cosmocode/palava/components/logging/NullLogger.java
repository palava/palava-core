package de.cosmocode.palava.components.logging;

import org.hibernate.Session;
import org.json.extension.JSONEncoder;

class NullLogger extends PalavaLogger {

	@Override
	public void log(Session session, Long objectID, Class<?> objectType, Enum<? extends LogOperation> operation, String message, JSONEncoder json) {
		
	}

}