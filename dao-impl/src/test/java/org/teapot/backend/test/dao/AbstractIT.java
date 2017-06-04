package org.teapot.backend.test.dao;

import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.teapot.backend.test.dao.config.TestDatabaseConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@EnableTransactionManagement
@ContextConfiguration(classes = TestDatabaseConfig.class)
public abstract class AbstractIT {
}
