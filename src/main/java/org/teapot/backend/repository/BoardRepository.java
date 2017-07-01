package org.teapot.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.teapot.backend.model.Board;
import org.teapot.backend.model.Owner;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
public interface BoardRepository extends JpaRepository<Board, Long> {

    List<Board> findByOwner(Owner owner);

    Page<Board> findByOwner(Owner owner, Pageable pageable);
}
