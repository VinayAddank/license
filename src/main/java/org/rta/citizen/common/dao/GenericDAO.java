package org.rta.citizen.common.dao;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Session;

public interface GenericDAO<T> {

    public Session getSession();
    
    public void update(T entity);

    public void delete(Object obj);

    public List<T> getAll();
    
    public List<T> getAll(boolean isActive);
    
    public Serializable save(T entity);
    
    public void saveOrUpdate(T entity);
    
    public T getEntity(Class<T> clazz, Long id);
    public T getEntity(Class<T> clazz, Integer id);
}
