package com.ppdai.platform.das.codegen.service;

import com.google.common.collect.Lists;
import com.ppdai.platform.das.codegen.common.validates.chain.ValidatorChain;
import com.ppdai.platform.das.codegen.constant.Message;
import com.ppdai.platform.das.codegen.dao.LoginUserDao;
import com.ppdai.platform.das.codegen.dao.PermissionDao;
import com.ppdai.platform.das.codegen.dto.entry.das.LoginUser;
import com.ppdai.platform.das.codegen.dto.entry.das.PublicStrategy;
import com.ppdai.platform.das.codegen.dto.model.Paging;
import com.ppdai.platform.das.codegen.dto.model.page.ListResult;
import com.ppdai.platform.das.codegen.dto.view.PublicStrategyView;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import java.sql.SQLException;
import java.util.List;

import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {LoginUserDao.class, PermissionDao.class, Message.class, PermissionService.class})
public class PublicStrategyServiceTest {

    @Mock
    private PublicStrategyService publicStrategyService;


    @Before
    public void setUp() throws Exception {
        ValidatorChain chain = new ValidatorChain();
        LoginUser user = LoginUser.builder().id(1L).build();
        PublicStrategy publicStrategy = PublicStrategy.builder().id(1L).name("name").build();
        Errors errors = new BeanPropertyBindingResult(publicStrategy, "publicStrategy", true, 256);
        when(publicStrategyService.validatePermision(user, errors)).thenReturn(chain);

        ListResult<PublicStrategyView> listResult = new ListResult<>();
        List<PublicStrategyView> list = Lists.newArrayList(PublicStrategyView.builder().id(1L).className("list").build());
        listResult.setList(list);
        Paging<PublicStrategy> paging = new Paging<>();
        paging.setData(new PublicStrategy());
        when(publicStrategyService.findPublicShardingStrategyPageList(paging)).thenReturn(listResult);

        when(publicStrategyService.isNotExistByName(publicStrategy)).thenReturn(true);
    }

    @Test
    public void validatePermisionTest() throws SQLException {
        LoginUser user = LoginUser.builder().id(1L).build();
        PublicStrategy publicStrategy = PublicStrategy.builder().id(1L).name("name").build();
        Errors errors = new BeanPropertyBindingResult(publicStrategy, "publicStrategy", true, 256);
        Assert.assertTrue(publicStrategyService.validatePermision(user, errors).validate().isValid());
    }

    @Test
    public void findPublicShardingStrategyPageListTest() throws SQLException {
        Paging<PublicStrategy> paging = new Paging<>();
        paging.setData(new PublicStrategy());
        Assert.assertTrue(publicStrategyService.findPublicShardingStrategyPageList(paging).getList().size() > 0);
    }

    @Test
    public void isNotExistByNameTest() throws SQLException {
        PublicStrategy publicStrategy = PublicStrategy.builder().id(1L).name("name").build();
        Assert.assertTrue(publicStrategyService.isNotExistByName(publicStrategy));
    }

}
