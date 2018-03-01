package org.rta.citizen.common.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

public abstract class BaseDAO<T> implements GenericDAO<T> {

	@Autowired
	private SessionFactory sessionFactory;
	protected Session session;
	private Class<T> persistObject;

	public void setSession(Session session) {
		this.session = session;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public Session getSession() {
		if (this.session == null || !this.session.isOpen() || this.session.isConnected())
			this.session = sessionFactory.getCurrentSession();
		return this.session;
	}

	public BaseDAO(Class<T> persistentClass) {
		this.persistObject = persistentClass;
	}

	public Class<T> getPersistentClass() {
		return persistObject;
	}

	public void update(T entity) {

		getSession().update(entity);

	}

    public void delete(Object obj) {
        getSession().delete(obj);
	}

	@SuppressWarnings("unchecked")
	public List<T> getAll() {
		List<T> getList = new ArrayList<T>();
		try {
			Criteria criteria = getSession().createCriteria(getPersistentClass());
			getList = criteria.list();
		} catch (HibernateException e) {
			e.printStackTrace();
		}
		return getList;
	}

	@SuppressWarnings("unchecked")
	public List<T> getAll(boolean isActive) {
		List<T> getList = new ArrayList<T>();
		try {
			Criteria criteria = getSession().createCriteria(getPersistentClass());
			criteria.add(Restrictions.eq("status", isActive));
			getList = criteria.list();
		} catch (HibernateException e) {
			e.printStackTrace();
		}
		return getList;
	}

	public Serializable save(T entity) {
		return (Long) getSession().save(entity);

	}

	public void saveOrUpdate(T entity) {

		getSession().saveOrUpdate(entity);

	}

	@Override
	@Transactional
	@SuppressWarnings("unchecked")
	public T getEntity(Class<T> clazz, Long id) {
		return (T) getSession().get(clazz, id);
	}

    @SuppressWarnings("unchecked")
    @Override
    public T getEntity(Class<T> clazz, Integer id) {
        return (T) getSession().get(clazz, id);
    }
	
	
}
