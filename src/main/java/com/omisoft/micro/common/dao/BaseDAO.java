package com.omisoft.micro.common.dao;


import com.omisoft.micro.common.entities.BaseEntity;
import com.omisoft.micro.common.exceptions.DataBaseException;
import com.omisoft.micro.common.exceptions.NotFoundException;
import java.math.BigInteger;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;

/**
 * Base DAO Impl Created by nslavov on 3/17/16.
 */
@Slf4j
public abstract class BaseDAO<T extends BaseEntity> {

  private final Class<T> type;
  @Inject
  private EntityManager entityManager;

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
    EntityManager session = getEntityManager();
    Query q = session.createQuery("Select t from " + type.getSimpleName() + " t");
    List<T> found = q.getResultList();
    if (found != null) {
      return found;
    } else {
      throw new NotFoundException(
          "Could not find all com.omisoft.micro.common.entities from type: " + type.getSimpleName()
              + "\n" + q.toString());
    }
  }


  /**
   * Updates entity.
   *
   * @param type type
   * @return updated entity
   */
  public T update(T type) throws DataBaseException {
    EntityManager session = getEntityManager();
    session.getTransaction().begin();

    T update = session.merge(type);
    session.getTransaction().commit();

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
    EntityManager session = getEntityManager();
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

  /**
   * Saves an entity
   *
   * @param entity to save
   * @return attached entity
   * @throws DataBaseException error
   */
  public T saveOrUpdate(T entity) throws DataBaseException {
    EntityManager session = getEntityManager();
    try {
      session.getTransaction().begin();
      entity = session.merge(entity);
      session.getTransaction().commit();
      return entity;
    } catch (Throwable e) {
      session.getTransaction().rollback();

      log.error("Error saving entity", e);
      throw new DataBaseException(e);
    }


  }

  /**
   * Saves or updates all com.omisoft.micro.common.entities from collection.
   *
   * @param collection collection to save.
   */
  public void saveAll(Collection<T> collection) throws DataBaseException {
    EntityManager session = getEntityManager();
    try {
      session.getTransaction().begin();
      collection.stream().forEach(entity -> {
        if (entity.getId() == null) {
          session.persist(entity);
        } else {
          entity = session.merge(entity);
        }
      });
      session.getTransaction().commit();

    } catch (Exception e) {
      session.getTransaction().rollback();

      log.error("ERROR IN SAVE ALL", e);
      throw new DataBaseException(e);

    }
  }

  /**
   * Base method to get entity manager Use this to enable com.omisoft.micro.common.filters
   */

  public EntityManager getEntityManager() {

    return this.entityManager;
  }

  @SuppressWarnings(value = "unchecked")
  public List<T> findInRange(int firstResult, int maxResults) throws NotFoundException {
    EntityManager session = getEntityManager();
    Query query = session.createQuery("Select t from " + type.getSimpleName() + " t");
    return (List<T>) query.setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
  }

  public long count() {
    EntityManager session = getEntityManager();

    Long count = (Long) session.createQuery("select count(t) from " + type.getSimpleName() + " t")
        .getSingleResult();
    return count;
  }

  public void persist(T type) throws DataBaseException {
    EntityManager session = getEntityManager();
    try {
      session.getTransaction().begin();

      session.persist(type);
      session.getTransaction().commit();

    } catch (Exception e) {
      session.getTransaction().rollback();
      log.error("ERROR PERSISTING", e);
      throw new DataBaseException(e);
    }
  }

  public void persistAll(Collection<T> type) throws DataBaseException {
    EntityManager session = getEntityManager();
    try {
      session.getTransaction().begin();

      for (T entity : type) {
        if (entity.getId() == null) {
          session.persist(entity);
        }
      }
      session.getTransaction().commit();

    } catch (Exception e) {
      session.getTransaction().rollback();

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

    EntityManager session = getEntityManager();
    try {
      session.getTransaction().begin();

      session.remove(findById(id));
      session.getTransaction().commit();

    } catch (Throwable e) {
      session.getTransaction().rollback();

      throw new DataBaseException("Can't delete id:" + id, e);
    }
  }

  /**
   * Finds all records, both active and not
   */
  public List<T> findAllUnfiltered() {
    EntityManager session = getEntityManager();

    ((Session) session.getDelegate()).disableFilter(BaseEntity.IS_ACTIVE_FILTER_NAME);
    Query q = session.createQuery("Select t from " + type.getSimpleName() + " t");
    return (List<T>) q.getResultList();
  }

  public Long getNextSequenceKey(String sequence) {

    EntityManager entityManager = getEntityManager();
    Query query = entityManager.createNativeQuery("SELECT nextval('public." + sequence + "');");
    BigInteger queryResults = (BigInteger) query.getSingleResult();
    Long k = queryResults.longValue();
    return k;

  }
}
