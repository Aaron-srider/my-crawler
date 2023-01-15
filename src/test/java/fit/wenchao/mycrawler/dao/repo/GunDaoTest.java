package fit.wenchao.mycrawler.dao.repo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class GunDaoTest {

    @Autowired
    GunDao gunDao;

    @Test
    public void test() {
        System.out.println(gunDao.list());
    }

}