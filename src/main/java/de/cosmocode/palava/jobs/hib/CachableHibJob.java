package de.cosmocode.palava.jobs.hib;

import java.util.Map;

import org.hibernate.Session;
import org.hibernate.Transaction;

import de.cosmocode.palava.CachableJob;
import de.cosmocode.palava.ConnectionLostException;
import de.cosmocode.palava.Request;
import de.cosmocode.palava.Response;
import de.cosmocode.palava.Server;
import de.cosmocode.palava.components.hib.Hib;

public abstract class CachableHibJob extends CachableJob {
    
	public static final String CADDY_HIBSESSION = "HibSession";
	
	@Override
	public final void process(Request request, Response response, Server server, de.cosmocode.palava.Session s,
			Map<String, Object> caddy) throws ConnectionLostException, Exception {

		Session session = (Session) caddy.get(CADDY_HIBSESSION);
		if (session == null) session = createHibSession(server, caddy);
		process(request, response, s, server, caddy, session);

	}
	
	public static org.hibernate.Session createHibSession(Server server, Map<String, Object> caddy)  {
		
		Hib hib = server.components.lookup(Hib.class);
		Session session = hib.getSessionFactory().openSession();
		caddy.put(CADDY_HIBSESSION, session);
		
		return session;
	}
	
	public final void flush (Session session) throws Exception {
        Transaction tx = session.beginTransaction();
        try {
            session.flush();
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            throw e;
        }
	}


	public abstract void process(Request request, Response response, de.cosmocode.palava.Session s, Server server,
        Map<String, Object> caddy, Session session) throws Exception;

}