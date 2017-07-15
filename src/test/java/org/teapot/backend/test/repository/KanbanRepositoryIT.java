package org.teapot.backend.test.repository;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.teapot.backend.model.kanban.Kanban;
import org.teapot.backend.model.organization.Organization;
import org.teapot.backend.repository.KanbanRepository;
import org.teapot.backend.repository.organization.OrganizationRepository;
import org.teapot.backend.test.AbstractIT;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class KanbanRepositoryIT extends AbstractIT {

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private KanbanRepository kanbanRepository;

    private Organization owner = new Organization();
    private Kanban kanban = new Kanban("findByOwnerTestKanban", owner);

    @Before
    public void setupKanban() {
        owner.setName("findByOwnerTest");
        organizationRepository.save(owner);

        kanbanRepository.save(kanban);
    }

    @Test
    public void findByOwnerIdTest() {
        assertEquals(Arrays.asList(kanban),
                kanbanRepository.findByOwnerId(owner.getId(), null).getContent());
    }

    @Test
    public void findByOwnerNameTest() {
        assertEquals(Arrays.asList(kanban),
                kanbanRepository.findByOwnerName(owner.getName(), null).getContent());
    }
}
