package org.teapot.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;
import org.teapot.backend.model.Owner;
import org.teapot.backend.model.OwnerItem;
import org.teapot.backend.model.kanban.Kanban;

import java.util.List;

@NoRepositoryBean
public interface AbstractOwnerItemRepository<T extends OwnerItem> extends JpaRepository<T, Long> {

    List<T> findByOwner(Owner owner);

    @RestResource(path = "find-by-owner-id")
    Page<Kanban> findByOwnerId(@Param("ownerId") Long ownerId, Pageable pageable);

    @RestResource(path = "find-by-owner-name")
    Page<Kanban> findByOwnerName(@Param("ownerName") String ownerName, Pageable pageable);
}
