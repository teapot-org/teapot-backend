package org.teapot.backend.test.controller;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;
import org.teapot.backend.model.Board;
import org.teapot.backend.model.organization.Organization;
import org.teapot.backend.repository.BoardRepository;
import org.teapot.backend.repository.organization.OrganizationRepository;

import java.util.List;

import static java.lang.String.format;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.teapot.backend.controller.BoardController.BOARDS_ENDPOINT;
import static org.teapot.backend.controller.BoardController.SINGLE_BOARD_ENDPOINT;

public class BoardControllerIT extends AbstractControllerIT {

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private BoardRepository boardRepository;

    private Organization savedBoardOwner = new Organization();

    private Board savedBoard = new Board("savedBoard", savedBoardOwner);

    private List<Board> allBoards;

    static ResultActions isBoardJsonAsExpected(ResultActions resultActions, String jsonPath, Board board)
            throws Exception {
        return resultActions.andExpect(jsonPath(jsonPath + ".title", is(board.getTitle())));
    }

    @Before
    public void addTestUsers() {
        savedBoardOwner.setName("getBoard1Owner");
        organizationRepository.save(savedBoardOwner);

        boardRepository.save(savedBoard);
    }

    @Test
    public void getBoardsTest() throws Exception {
        allBoards = boardRepository.findAll();

        ResultActions result = mockMvc.perform(get(BOARDS_ENDPOINT))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$._embedded.boards", hasSize(allBoards.size())));

        for (int i = 0; i < allBoards.size(); i++) {
            isBoardJsonAsExpected(result, format("$._embedded.boards[%d]", i), allBoards.get(i));
        }
    }

    @Test
    public void getSingleBoardByIdTest() throws Exception {
        ResultActions result = mockMvc.perform(get(SINGLE_BOARD_ENDPOINT, savedBoard.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType));

        isBoardJsonAsExpected(result, "$", savedBoard);
    }

    @Test
    public void getNotExistsBoardByIdTest() throws Exception {
        mockMvc.perform(get(SINGLE_BOARD_ENDPOINT, -1))
                .andExpect(status().isNotFound());
    }
}
