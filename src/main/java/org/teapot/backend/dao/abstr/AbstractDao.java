package org.teapot.backend.dao.abstr;

import java.io.Serializable;
import java.util.List;

public interface AbstractDao<T extends Serializable> {

    void insert(T entity);

    T getById(Long id);

    List<T> getList();

    List<T> getList(Integer offset, Integer count);

    void update(T entity);

    void delete(T entity);

    void deleteById(Long id);
}
