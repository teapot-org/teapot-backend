package org.teapot.backend.test.repository;

import org.assertj.core.util.Lists;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.teapot.backend.model.Board;
import org.teapot.backend.model.Owner;
import org.teapot.backend.model.organization.Organization;
import org.teapot.backend.repository.BoardRepository;
import org.teapot.backend.repository.OwnerRepository;
import org.teapot.backend.test.AbstractIT;

public class BoardRepositoryIT extends AbstractIT {

    @Autowired
    private OwnerRepository ownerRepository;

    @Autowired
    private BoardRepository boardRepository;

    private Owner owner = new Organization();
    private Board board = new Board("findByOwnerTestBoard", owner);

    @Before
    public void setupBoard() {
        owner.setName("findByOwnerTest");
        ownerRepository.save(owner);

        boardRepository.save(board);
    }

    @Test
    public void findByOwnerTest() {
        Assert.assertEquals(Lists.newArrayList(board),
                boardRepository.findByOwner(owner, new PageRequest(1, 20)).getContent());
    }
}
