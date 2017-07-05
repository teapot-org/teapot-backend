package org.teapot.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.teapot.backend.model.Board;
import org.teapot.backend.model.Owner;

public interface BoardRepository extends JpaRepository<Board, Long> {

    Page<Board> findByOwner(Owner owner, Pageable pageable);
}
