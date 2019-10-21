package com.ppdai.platform.das.codegen.dao;


import com.ppdai.platform.das.codegen.dto.entry.das.PublicStrategy;
import com.ppdai.platform.das.codegen.dto.model.Paging;
import com.ppdai.platform.das.codegen.dto.view.PublicStrategyView;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {PublicStrategyDao.class})
public class PublicStrategyDaoTest {

    @Autowired
    PublicStrategyDao publicStrategyDao;

    PublicStrategy publicStrategy;

    PublicStrategy publicStrategyModel;

    Paging<PublicStrategy> paging;

    @Before
    public void setUp() {
        paging = new Paging<>();
        publicStrategyModel = PublicStrategy.builder().className("PublicStrategyDaoTest").build();
        publicStrategy = PublicStrategy.builder().name("publicStrategy").comment("project comment").strategyLoadingType(1).strategyParams("aaa").strategySource("sasas").className("PublicStrategyDaoTest").build();
        paging.setData(publicStrategyModel);
    }

    @Test
    public void insertPublicStrategy() throws Exception {
        Long id = publicStrategyDao.insertPublicStrategy(publicStrategy);
        System.out.println("insertPublicStrategy :-------> " + id);
        Assert.assertTrue(id > 0);
    }

    @Test
    public void updatePublicStrategy() throws Exception {
        publicStrategy.setId(1L);
        publicStrategy.setStrategySource("das.codegen.dto.entry.das.PublicStrategy");
        int count = publicStrategyDao.updatePublicStrategy(publicStrategy);
        System.out.println("updatePublicStrategy :-------> " + count);
        Assert.assertTrue(count > 0);
    }

    @Test
    public void getPublicStrategyById() throws Exception {
        PublicStrategy publicStrategy = publicStrategyDao.getPublicStrategyById(1L);
        System.out.println("getPublicStrategyById :-------> " + publicStrategy);
        Assert.assertTrue(publicStrategy != null);
    }

    @Test
    public void getCountByName() throws Exception {
        Long count = publicStrategyDao.getCountByName("publicStrategy");
        System.out.println("getCountByName :-------> " + count);
        Assert.assertTrue(count > 0);
    }

    @Test
    public void getCountByIdAndName() throws Exception {
        Long count = publicStrategyDao.getCountByIdAndName(2L, "publicStrategy");
        System.out.println("getCountByIdAndName :-------> " + count);
        Assert.assertTrue(count > 0);
    }

    @Test
    public void deletePublicStrategy() throws Exception {
        publicStrategy.setId(2L);
        int count = publicStrategyDao.deletePublicStrategy(publicStrategy);
        System.out.println("deletePublicStrategy :-------> " + count);
        Assert.assertTrue(count > 0);
    }

    @Test
    public void getAllPublicStrateges() throws Exception {
        List<PublicStrategy> list = publicStrategyDao.getAllPublicStrateges();
        System.out.println("getAllPublicStrateges :-------> " + list);
        Assert.assertTrue(list.size() > 0);
    }

    @Test
    public void getPublicStrategyTotalCount() throws Exception {
        Long count = publicStrategyDao.getPublicStrategyTotalCount(paging);
        System.out.println("getPublicStrategyTotalCount :-------> " + count);
        Assert.assertTrue(count > 0);
    }

    @Test
    public void findPublicStrategyPageList() throws Exception {
        List<PublicStrategyView> list = publicStrategyDao.findPublicStrategyPageList(paging);
        System.out.println("findPublicStrategyPageList :-------> " + list);
        Assert.assertTrue(list.size() > 0);
    }
}
