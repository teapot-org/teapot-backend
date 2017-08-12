package org.teapot.backend.repository;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RestResource;
import org.teapot.backend.model.BaseEntity;

import java.util.Collection;
import java.util.List;

@NoRepositoryBean
public interface BaseEntityRepository<T extends BaseEntity> extends PagingAndSortingRepository<T, Long> {

    String HAS_ADMIN_ROLE = "1 = ?#{principal?.hasRole('ADMIN') ? 1 : 0}";

    @Override
    List<T> findAll();

    @Override
    @RestResource(exported = false)
    <S extends T> Collection<S> save(Iterable<S> iterable);

    @Override
    @RestResource(exported = false)
    void deleteAll();

    @Override
    @RestResource(exported = false)
    void delete(Iterable<? extends T> entities);
}
