package de.cosmocode.palava;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.log4j.Logger;


/**
 * manages the session map
 * @author Detlef Hüttemann
 */
public class SessionManager
{
    private static Logger logger = Logger.getLogger( SessionManager.class ) ;


	private Map<String,Session> sessions = new HashMap<String,Session>();


	synchronized public Session get(String session_id)
	{
		return sessions.get(session_id);
	}

    public void remove( String id ) {
        sessions.remove( id ) ;
    }


    public void purge( int seconds ) {
        // two-phase-cleanup:
        //   1. collect and remove purged (synchronized)
        //   2. delete purged

        Set<Session> purged = new HashSet<Session>();
        Date testDate = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime( testDate );
        cal.add( Calendar.SECOND, -seconds );
        synchronized (this) {
            for ( Iterator<Session> iter=sessions.values().iterator(); iter.hasNext(); ) {
                Session session = iter.next();
                if ( session.getAccessTime().before( testDate ) ) 
                    purged.add( session );
                iter.remove();
            }
        }
        for ( Iterator<Session> iter=purged.iterator(); iter.hasNext(); ) {
            Session session = iter.next();
            logger.debug("purged " + session.toString() );
            session.invalidate();
        }
    }


	public Session createSession()
	{
		String session_id = "";
		Random rnd = new Random();
		for (int n = 0; n < 64;  n++)
		{
			session_id = session_id + rnd.nextInt(10);
		}
		Session session = new Session(session_id);
		sessions.put(session_id, session);
		return session;
	}

    public Map<String,Session> getSessions() {
        return sessions;
    }

}
