package by.hzv.s2.dao;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.annotations.Test;


@Test(suiteName = "integration")
@ContextConfiguration("/config/spring/simple-storage-context.xml")
class SimpleJdbcDataStoreRepositoryTest extends AbstractTransactionalTestNGSpringContextTests {

    @Test
    void find() {
        //TODO implement test
    }

    @Test
    void save() {
        //TODO implement test
    }
}

