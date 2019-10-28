package com.ppdai.das.console.dao;

import com.ppdai.das.console.dao.DataBaseDao;
import com.ppdai.das.console.dao.GroupDao;
import com.ppdai.das.console.dao.LoginUserDao;
import com.ppdai.das.console.dto.entry.das.DasGroup;
import com.ppdai.das.console.dto.entry.das.DataBaseInfo;
import com.ppdai.das.console.dto.entry.das.LoginUser;
import com.ppdai.das.console.dto.model.Paging;
import com.ppdai.das.console.dto.view.DalGroupView;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = {LoginUserDao.class, GroupDao.class})
public class BeanGetterTest {

    @Autowired
    LoginUserDao loginUserDao;

    @Autowired
    GroupDao groupDao;


    @Test
    public void getAllUsersTest() throws Exception{
        List<LoginUser> list = loginUserDao.getAllUsers();
        System.out.println(list);
    }

    @Test
    public void addUser() throws Exception{
        LoginUserDao loginUserDao = new LoginUserDao();
        Long id = loginUserDao.insertUser(LoginUser.builder()
                .userName("tmom")
                .userNo("121212")
                .password("1212121212")
                .userEmail("asasas@212.com")
                            .build());
        System.out.println("---------> " + id);
    }

    @Test
    public void addDalGroupDBDao() throws Exception{
        DataBaseDao dalGroupDBDao = new DataBaseDao();
        Long id = dalGroupDBDao.insertDataBaseInfo(DataBaseInfo.builder()
                .dbname("asasas")
                .dal_group_id(1L)
                .db_address("121212")
                .db_port("21212")
                .db_user("asas")
                .db_password("12121")
                .db_catalog("121212")
                .build());
        System.out.println("---------> " + id);
    }

    @Test
    public void groupDaoTest() throws Exception{
        Paging paging = new Paging();
        paging.setSort(" id asc");
        List<DasGroup> list= groupDao.findGroupPageList(paging);
        System.out.println(list);
    }

    @Test
    public void getTotalCountTest() throws Exception{
        Paging paging = new Paging();
        //paging.setData(new DasGroup());
        paging.setSort(" id asc");
        Long i = groupDao.getTotalCount(paging);
        System.out.println(i);
    }

    @Test
    public void groupServiceTest() throws Exception{
        Paging<DasGroup> paging = new Paging();
        //paging.setData(new DasGroup());
        paging.setSort(" id asc");
        List<DalGroupView> list = groupDao.findGroupPageList(paging);
        System.out.println(list);
    }
}
