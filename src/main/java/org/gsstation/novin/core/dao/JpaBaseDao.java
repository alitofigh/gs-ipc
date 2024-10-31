package org.gsstation.novin.core.dao;

import org.gsstation.novin.core.exception.GeneralDatabaseException;

import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by A_Tofigh at 07/22/2024
 */
public abstract class JpaBaseDao<T> extends BaseDao<T> {
    protected Class<T> entityType;

    public JpaBaseDao(Class<T> entityType, String databaseInstanceName) {
        super(databaseInstanceName);
        this.entityType = entityType;
    }

    public void store(T entity) throws GeneralDatabaseException {
        EntityManager entityManager = null;
        try {
            entityManager = EntityManagerFactory.newEntityManager(
                    databaseInstanceName, entity);
            entityManager.setFlushMode(FlushModeType.COMMIT);
            entityManager.getTransaction().begin();
            entityManager.persist(entity);
            entityManager.getTransaction().commit();
        } catch (GeneralDatabaseException e) {
            throw e;
        } catch (Exception e) {
            throw new GeneralDatabaseException(e);
        } finally {
            if (entityManager != null)
                entityManager.close();
        }
    }

    public void store(List<T> entities) throws GeneralDatabaseException {
        EntityManager entityManager = null;
        try {
            entityManager = EntityManagerFactory.newEntityManager(
                    databaseInstanceName, entities.get(0));
            entityManager.setFlushMode(FlushModeType.COMMIT);
            entityManager.getTransaction().begin();
            for (T entity : entities) {
                entityManager.persist(entity);
            }
            entityManager.getTransaction().commit();
        } catch (GeneralDatabaseException e) {
            if (entityManager != null)
                entityManager.getTransaction().rollback();
            throw e;
        } catch (Exception e) {
            throw new GeneralDatabaseException(e);
        } finally {
            if (entityManager != null)
                entityManager.close();
        }
    }

    /*public <T extends MyClass> Collection<T> bulkSave(Collection<T> entities) {
        final List<T> savedEntities = new ArrayList<T>(entities.size());
        int i = 0;
        for (T t : entities) {
            savedEntities.add(persistOrMerge(t));
            i++;
            if (i % batchSize == 0) {
                // Flush a batch of inserts and release memory.
                entityManager.flush();
                entityManager.clear();
            }
        }
        // Flush one last time to catch those beyond that last full batch.
        entityManager.flush();
        entityManager.clear();
        return savedEntities;
    }

    private <T extends MyClass> T persistOrMerge(T t) {
        if (t.getId() == null) {
            entityManager.persist(t);
            return t;
        } else {
            return entityManager.merge(t);
        }
    }*/

    public int update(T entity) throws GeneralDatabaseException {
        EntityManager entityManager = null;
        T mergedMessage;
        try {
            entityManager = EntityManagerFactory.newEntityManager(
                    databaseInstanceName, entity);
            entityManager.setFlushMode(FlushModeType.COMMIT);
            entityManager.getTransaction().begin();
            mergedMessage = entityManager.merge(entity);
            //entityManager.flush();
            entityManager.getTransaction().commit();
            if (mergedMessage != null)
                return 1;
            else
                return 0;
        } finally {
            if (entityManager != null)
                entityManager.close();
        }
    }

    public int delete(T entity) throws GeneralDatabaseException {
        EntityManager entityManager = null;
        try {
            entityManager = EntityManagerFactory.newEntityManager(
                    databaseInstanceName, entity);
            entityManager.setFlushMode(FlushModeType.COMMIT);
            entityManager.getTransaction().begin();
            entityManager.remove(entityManager.contains(entity) ? entity
                    : entityManager.merge(entity));
            entityManager.getTransaction().commit();
        } finally {
            if (entityManager != null)
                entityManager.close();
        }
        return 1;
    }

    public long getTotalCount() throws GeneralDatabaseException {
        EntityManager entityManager = null;
        try {
            entityManager = EntityManagerFactory.newEntityManager(
                    databaseInstanceName, null);
            CriteriaBuilder criteriaBuilder =
                    entityManager.getCriteriaBuilder();
            CriteriaQuery<Long> criteriaQuery =
                    criteriaBuilder.createQuery(Long.class);
            criteriaQuery.select(criteriaBuilder.count(
                    criteriaQuery.from(entityType)));
            return entityManager.createQuery(criteriaQuery).getSingleResult();
        } finally {
            if (entityManager != null)
                entityManager.close();
        }
    }
}
