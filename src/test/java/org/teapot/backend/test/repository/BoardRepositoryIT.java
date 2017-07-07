package org.teapot.backend.test.repository;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.teapot.backend.model.Board;
import org.teapot.backend.model.organization.Organization;
import org.teapot.backend.repository.BoardRepository;
import org.teapot.backend.repository.organization.OrganizationRepository;
import org.teapot.backend.test.AbstractIT;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class BoardRepositoryIT extends AbstractIT {

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private BoardRepository boardRepository;

    private Organization owner = new Organization();
    private Board board = new Board("findByOwnerTestBoard", owner);

    @Before
    public void setupBoard() {
        owner.setName("findByOwnerTest");
        organizationRepository.save(owner);

        boardRepository.save(board);
    }

    @Test
    public void findByOwnerIdTest() {
        assertEquals(Arrays.asList(board),
                boardRepository.findByOwnerId(owner.getId(), null).getContent());
    }

    @Test
    public void findByOwnerNameTest() {
        assertEquals(Arrays.asList(board),
                boardRepository.findByOwnerName(owner.getName(), null).getContent());
    }
}
