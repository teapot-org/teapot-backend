package org.teapot.backend.service.abstr;


public interface AbstractService<T> {

    T getById(long id);
}
