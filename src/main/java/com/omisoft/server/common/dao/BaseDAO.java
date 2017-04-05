package com.omisoft.server.common.dao;


import com.omisoft.server.common.entities.BaseEntity;
import com.omisoft.server.common.exceptions.DataBaseException;
import com.omisoft.server.common.exceptions.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import javax.inject.Inject;
import javax.persistence.Query;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * Base DAO Impl Created by nslavov on 3/17/16.
 */
@Slf4j
public abstract class BaseDAO<T extends BaseEntity> {

  private final Class<T> type;

  private Session session;
  @Inject
  private SessionFactory sessionFactory;

  protected BaseDAO(Class<T> type) {
    this.type = type;
  }

  /**
   * Finds all elements.
   *
   * @return all elements
   */
  @SuppressWarnings(value = "unchecked")
  public List<T> findAll() throws NotFoundException {
    Session session = getSession();
    Query q = session.createQuery("Select t from " + type.getSimpleName() + " t");
    List<T> found = q.getResultList();
    if (found != null) {
      return found;
    } else {
      throw new NotFoundException(
          "Could not find all com.omisoft.server.common.entities from type: " + type.getSimpleName() + "\n" + q.toString());
    }
  }


  /**
   * Updates entity.
   *
   * @param type type
   * @return updated entity
   */
  public T update(T type) throws DataBaseException {
    Session session = getSession();

    T update = (T) session.merge(type);
    if (update != null) {
      return update;
    } else {
      throw new DataBaseException(
          "Could not update entity from type: " + this.type.getSimpleName());
    }

  }


  /**
   * Finds by ID one element.
   *
   * @param id to search for
   * @return entity
   */

  public T findById(UUID id) throws NotFoundException, DataBaseException {
    Session session = getSession();
    try {
      T found = session.find(type, id);
      if (found != null) {
        return found;
      }
      throw new NotFoundException(
          "Could not find by id: " + id + " entity from type: " + type.getSimpleName());
    } catch (Exception e) {
      throw new DataBaseException(e);

    }
  }


  public T saveOrUpdate(T entity) throws DataBaseException {
    Session session = getSession();
    session.saveOrUpdate(entity);

    return entity;

  }

  /**
   * Saves or updates all com.omisoft.server.common.entities from collection.
   *
   * @param collection collection to save.
   */
  public void saveAll(Collection<T> collection) throws DataBaseException {
    Session session = getSession();
    try {
      collection.stream().forEach(entity -> {
        if (entity.getId() == null) {
          session.persist(entity);
        } else {
          session.merge(entity);
        }
      });
    } catch (Exception e) {
      log.error("ERROR IN SAVE ALL", e);
      throw new DataBaseException(e);

    }
  }

  /**
   * Base method to get entity manager Use this to enable com.omisoft.server.common.filters
   *
   * @return
   */

  public Session getSession() {
    Session session = sessionFactory.getCurrentSession();
//    Filter isActiveFilter = session.enableFilter(BaseEntity.IS_ACTIVE_FILTER_NAME);
//    isActiveFilter.setParameter("isActive", Boolean.TRUE);
    return session;
  }

  @SuppressWarnings(value = "unchecked")

  public List<T> findInRange(int firstResult, int maxResults) throws NotFoundException {
    Session session = getSession();
    Query query = session.createQuery("Select t from " + type.getSimpleName() + " t");
    return (List<T>) query.setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
  }

  public long count() {
    Session session = getSession();

    Long count = (Long) session.createQuery("select count(t) from " + type.getSimpleName() + " t")
        .getSingleResult();
    return count;
  }

  public void persist(T type) throws DataBaseException {
    Session session = getSession();
    try {
      session.persist(type);
    } catch (Exception e) {
      log.error("ERROR PERSISTING", e);
      throw new DataBaseException(e);
    }
  }

  public void persistAll(Collection<T> type) throws DataBaseException {
    Session session = getSession();
    try {

      for (T entity : type) {
        if (entity.getId() == null) {
          session.persist(entity);
        }
      }
    } catch (Exception e) {
      log.error("ERROR!", e);
      throw new DataBaseException(e);
    }
  }

  public void remove(T entity) throws DataBaseException {

//    entity.setIsActive(Boolean.FALSE);
    physicalDelete(entity.getId());
  }

  public void removeById(UUID id) throws NotFoundException, DataBaseException {
//    entity.setIsActive(Boolean.FALSE);
    physicalDelete(id);
  }

  public void physicalDelete(UUID id) throws DataBaseException {

    Session session = getSession();
    try {
      session.remove(findById(id));
    } catch (Throwable e) {
      throw new DataBaseException("Can't delete id:" + id, e);
    }
  }

  /**
   * Finds all records, both active and not
   */
  public List<T> findAllUnfiltered() {
    Session session = getSession();

    ((Session) session.getDelegate()).disableFilter(BaseEntity.IS_ACTIVE_FILTER_NAME);
    Query q = session.createQuery("Select t from " + type.getSimpleName() + " t");
    return (List<T>) q.getResultList();
  }
}
