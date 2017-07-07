package org.teapot.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;
import org.teapot.backend.model.Board;

public interface BoardRepository extends JpaRepository<Board, Long> {

    @RestResource(path = "find-by-owner-id")
    Page<Board> findByOwnerId(@Param("ownerId") Long ownerId, Pageable pageable);

    @RestResource(path = "find-by-owner-name")
    Page<Board> findByOwnerName(@Param("ownerName") String ownerName, Pageable pageable);
}
