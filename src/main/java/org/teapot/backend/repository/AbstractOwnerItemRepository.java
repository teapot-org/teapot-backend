package org.teapot.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;
import org.teapot.backend.model.Owner;
import org.teapot.backend.model.OwnerItem;

import java.util.List;

@NoRepositoryBean
public interface AbstractOwnerItemRepository<T extends OwnerItem> extends BaseEntityRepository<T> {

    @RestResource(exported = false)
    List<T> findByOwner(Owner owner);

    @RestResource(exported = false)
    Page<T> findByOwnerId(@Param("ownerId") Long ownerId, Pageable pageable);
}
