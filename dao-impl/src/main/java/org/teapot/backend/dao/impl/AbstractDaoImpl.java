package org.teapot.backend.dao.impl;

import org.springframework.transaction.annotation.Transactional;
import org.teapot.backend.dao.abstr.AbstractDao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.List;

@Transactional
public abstract class AbstractDaoImpl<T extends Serializable>
        implements AbstractDao<T> {

    @PersistenceContext
    EntityManager entityManager;

    private final Class<T> clazz;

    public AbstractDaoImpl(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public void insert(T entity) {
        entityManager.persist(entity);
    }

    @Override
    public T getById(Long id) {
        return entityManager.find(clazz, id);
    }

    @Override
    public List<T> getList() {
        return entityManager
                .createQuery("FROM " + clazz.getName(), clazz)
                .getResultList();
    }

    @Override
    public List<T> getList(Integer offset, Integer count) {
        String query = "FROM " + clazz.getName();

        if ((offset != null) && (count != null)) {
            return entityManager
                    .createQuery(query, clazz)
                    .setFirstResult(offset)
                    .setMaxResults(count)
                    .getResultList();
        } else if (offset != null) {
            return entityManager
                    .createQuery(query, clazz)
                    .setFirstResult(offset)
                    .getResultList();
        } else if (count != null) {
            return entityManager
                    .createQuery(query, clazz)
                    .setMaxResults(count)
                    .getResultList();
        }

        return getList();
    }

    @Override
    public void update(T entity) {
        entityManager.merge(entity);
    }

    @Override
    public void delete(T entity) {
        entityManager.remove(entity);
    }

    @Override
    public void deleteById(Long id) {
        entityManager.remove(entityManager.find(clazz, id));
    }
}
