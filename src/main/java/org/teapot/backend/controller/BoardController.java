package org.teapot.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.teapot.backend.controller.exception.ResourceNotFoundException;
import org.teapot.backend.model.Board;
import org.teapot.backend.model.Owner;
import org.teapot.backend.repository.BoardRepository;
import org.teapot.backend.repository.OwnerRepository;

import java.util.List;

import static java.util.Optional.ofNullable;

@RestController
@RequestMapping("/boards")
public class BoardController {

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private OwnerRepository ownerRepository;

    @GetMapping
    public List<Board> getBoards(Pageable pageable) {
        return boardRepository.findAll(pageable).getContent();
    }

    @GetMapping(params = "owner")
    public List<Board> getOwnerBoards(
            @RequestParam("owner") Long ownerId,
            Pageable pageable
    ) {
        Owner owner = ofNullable(ownerRepository.findOne(ownerId))
                .orElseThrow(ResourceNotFoundException::new);

        return boardRepository.findByOwner(owner, pageable).getContent();
    }

    @GetMapping("/{id}")
    public Board getBoard(@PathVariable Long id) {
        return ofNullable(boardRepository.findOne(id))
                .orElseThrow(ResourceNotFoundException::new);
    }
}
