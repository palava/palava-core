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
package de.cosmocode.palava.components.hib;

import java.io.Serializable;
import java.sql.Connection;

import org.hibernate.CacheMode;
import org.hibernate.Criteria;
import org.hibernate.EntityMode;
import org.hibernate.Filter;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.ReplicationMode;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.jdbc.Work;
import org.hibernate.stat.SessionStatistics;

import de.cosmocode.palava.Closable;

public class ClosableSession implements Session, Closable {

	private static final long serialVersionUID = -1998276322962471556L;
	
	private final Session session;
	
    public ClosableSession(Session session) {
		this.session = session;
	}
	
	@Override
	public void onClose() {
	    if (session.isOpen()) session.close();
	}

    @Override
    public Transaction beginTransaction() throws HibernateException {
        return session.beginTransaction();
    }
    
    @Override
    public void cancelQuery() throws HibernateException {
        session.cancelQuery();
    }

    @Override
    public void clear() {
        session.clear();
    }

    @Override
    public Connection close() throws HibernateException {
        return session.close();
    }

    @Override
    @SuppressWarnings("deprecation")
    public Connection connection() throws HibernateException {
        return session.connection();
    }

    @Override
    public boolean contains(Object object) {
        return session.contains(object);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Criteria createCriteria(Class persistentClass, String alias) {
        return session.createCriteria(persistentClass, alias);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Criteria createCriteria(Class persistentClass) {
        return session.createCriteria(persistentClass);
    }

    @Override
    public Criteria createCriteria(String entityName, String alias) {
        return session.createCriteria(entityName, alias);
    }

    @Override
    public Criteria createCriteria(String entityName) {
        return session.createCriteria(entityName);
    }

    @Override
    public Query createFilter(Object collection, String queryString) throws HibernateException {
        return session.createFilter(collection, queryString);
    }

    @Override
    public Query createQuery(String queryString) throws HibernateException {
        return session.createQuery(queryString);
    }

    @Override
    public SQLQuery createSQLQuery(String queryString) throws HibernateException {
        return session.createSQLQuery(queryString);
    }

    @Override
    public void delete(Object object) throws HibernateException {
        session.delete(object);
    }

    @Override
    public void delete(String entityName, Object object) throws HibernateException {
        session.delete(entityName, object);
    }

    @Override
    public void disableFilter(String filterName) {
        session.disableFilter(filterName);
    }

    @Override
    public Connection disconnect() throws HibernateException {
        return session.disconnect();
    }

    @Override
    public void doWork(Work work) throws HibernateException {
        session.doWork(work);
    }

    @Override
    public Filter enableFilter(String filterName) {
        return session.enableFilter(filterName);
    }

    @Override
    public void evict(Object object) throws HibernateException {
        session.evict(object);
    }

    @Override
    public void flush() throws HibernateException {
        session.flush();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object get(Class clazz, Serializable id, LockMode lockMode) throws HibernateException {
        return session.get(clazz, id, lockMode);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object get(Class clazz, Serializable id) throws HibernateException {
        return session.get(clazz, id);
    }

    @Override
    public Object get(String entityName, Serializable id, LockMode lockMode) throws HibernateException {
        return session.get(entityName, id, lockMode);
    }

    @Override
    public Object get(String entityName, Serializable id) throws HibernateException {
        return session.get(entityName, id);
    }

    @Override
    public CacheMode getCacheMode() {
        return session.getCacheMode();
    }

    @Override
    public LockMode getCurrentLockMode(Object object) throws HibernateException {
        return session.getCurrentLockMode(object);
    }

    @Override
    public Filter getEnabledFilter(String filterName) {
        return session.getEnabledFilter(filterName);
    }

    @Override
    public EntityMode getEntityMode() {
        return session.getEntityMode();
    }

    @Override
    public String getEntityName(Object object) throws HibernateException {
        return session.getEntityName(object);
    }

    @Override
    public FlushMode getFlushMode() {
        return session.getFlushMode();
    }

    @Override
    public Serializable getIdentifier(Object object) throws HibernateException {
        return session.getIdentifier(object);
    }

    @Override
    public Query getNamedQuery(String queryName) throws HibernateException {
        return session.getNamedQuery(queryName);
    }

    @Override
    public Session getSession(EntityMode entityMode) {
        return session.getSession(entityMode);
    }

    @Override
    public SessionFactory getSessionFactory() {
        return session.getSessionFactory();
    }

    @Override
    public SessionStatistics getStatistics() {
        return session.getStatistics();
    }

    @Override
    public Transaction getTransaction() {
        return session.getTransaction();
    }

    @Override
    public boolean isConnected() {
        return session.isConnected();
    }

    @Override
    public boolean isDirty() throws HibernateException {
        return session.isDirty();
    }

    @Override
    public boolean isOpen() {
        return session.isOpen();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object load(Class theClass, Serializable id, LockMode lockMode) throws HibernateException {
        return session.load(theClass, id, lockMode);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object load(Class theClass, Serializable id) throws HibernateException {
        return session.load(theClass, id);
    }

    @Override
    public void load(Object object, Serializable id) throws HibernateException {
        session.load(object, id);
    }

    @Override
    public Object load(String entityName, Serializable id, LockMode lockMode) throws HibernateException {
        return session.load(entityName, id, lockMode);
    }

    @Override
    public Object load(String entityName, Serializable id) throws HibernateException {
        return session.load(entityName, id);
    }

    @Override
    public void lock(Object object, LockMode lockMode) throws HibernateException {
        session.lock(object, lockMode);
    }

    @Override
    public void lock(String entityName, Object object, LockMode lockMode) throws HibernateException {
        session.lock(entityName, object, lockMode);
    }

    @Override
    public Object merge(Object object) throws HibernateException {
        return session.merge(object);
    }

    @Override
    public Object merge(String entityName, Object object) throws HibernateException {
        return session.merge(entityName, object);
    }

    @Override
    public void persist(Object object) throws HibernateException {
        session.persist(object);
    }

    @Override
    public void persist(String entityName, Object object) throws HibernateException {
        session.persist(entityName, object);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void reconnect() throws HibernateException {
        session.reconnect();
    }

    @Override
    public void reconnect(Connection connection) throws HibernateException {
        session.reconnect(connection);
    }

    @Override
    public void refresh(Object object, LockMode lockMode) throws HibernateException {
        session.refresh(object, lockMode);
    }

    @Override
    public void refresh(Object object) throws HibernateException {
        session.refresh(object);
    }

    @Override
    public void replicate(Object object, ReplicationMode replicationMode) throws HibernateException {
        session.replicate(object, replicationMode);
    }

    @Override
    public void replicate(String entityName, Object object, ReplicationMode replicationMode) throws HibernateException {
        session.replicate(entityName, object, replicationMode);
    }

    @Override
    public Serializable save(Object object) throws HibernateException {
        return session.save(object);
    }

    @Override
    public Serializable save(String entityName, Object object) throws HibernateException {
        return session.save(entityName, object);
    }

    @Override
    public void saveOrUpdate(Object object) throws HibernateException {
        session.saveOrUpdate(object);
    }

    @Override
    public void saveOrUpdate(String entityName, Object object) throws HibernateException {
        session.saveOrUpdate(entityName, object);
    }

    @Override
    public void setCacheMode(CacheMode cacheMode) {
        session.setCacheMode(cacheMode);
    }

    @Override
    public void setFlushMode(FlushMode flushMode) {
        session.setFlushMode(flushMode);
    }

    @Override
    public void setReadOnly(Object entity, boolean readOnly) {
        session.setReadOnly(entity, readOnly);
    }

    @Override
    public void update(Object object) throws HibernateException {
        session.update(object);
    }

    @Override
    public void update(String entityName, Object object) throws HibernateException {
        session.update(entityName, object);
    }
	
}