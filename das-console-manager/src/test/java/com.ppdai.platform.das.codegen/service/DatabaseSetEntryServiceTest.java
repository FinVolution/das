package com.ppdai.platform.das.codegen.service;

import com.google.common.collect.Lists;
import com.ppdai.platform.das.codegen.dao.DataBaseSetEntryDao;
import com.ppdai.platform.das.codegen.dto.entry.das.DatabaseSetEntry;
import com.ppdai.platform.das.codegen.dto.model.Paging;
import com.ppdai.platform.das.codegen.dto.model.page.ListResult;
import com.ppdai.platform.das.codegen.dto.view.DatabaseSetEntryView;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.sql.SQLException;
import java.util.List;

import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {DataBaseSetEntryDao.class})
public class DatabaseSetEntryServiceTest {

    @Mock
    private DatabaseSetEntryService databaseSetEntryService;

    @Before
    public void setUp() throws Exception {
        ListResult<DatabaseSetEntryView> listResult = new ListResult<>();
        List<DatabaseSetEntryView> list = Lists.newArrayList(DatabaseSetEntryView.builder().id(1).dbName("tom").build());
        listResult.setList(list);
        Paging<DatabaseSetEntry> paging = new Paging<>();
        paging.setData(new DatabaseSetEntry());
        when(databaseSetEntryService.findDbSetEntryPageList(paging)).thenReturn(listResult);

        DatabaseSetEntry databaseSetEntry = DatabaseSetEntry.builder().db_Id(1L).build();
        when(databaseSetEntryService.isNotExistByName(databaseSetEntry)).thenReturn(true);

        DatabaseSetEntry dbsetEntry = DatabaseSetEntry.builder().db_Id(1L).build();
        when(databaseSetEntryService.insertDatabaseSetEntry(dbsetEntry)).thenReturn(true);
    }

    @Test
    public void findDbSetEntryPageListTest() throws SQLException {
        ListResult<DatabaseSetEntryView> listResult = new ListResult<>();
        List<DatabaseSetEntryView> list = Lists.newArrayList(DatabaseSetEntryView.builder().id(1).dbName("tom").build());
        listResult.setList(list);
        Paging<DatabaseSetEntry> paging = new Paging<>();
        paging.setData(new DatabaseSetEntry());
        Assert.assertTrue(databaseSetEntryService.findDbSetEntryPageList(paging).getList().size() == 1);
    }

    @Test
    public void isNotExistByNameTest() throws SQLException {
        DatabaseSetEntry databaseSetEntry = DatabaseSetEntry.builder().db_Id(1L).build();
        Assert.assertTrue(databaseSetEntryService.isNotExistByName(databaseSetEntry));
    }

    @Test
    public void insertDatabaseSetEntryTest() throws SQLException {
        DatabaseSetEntry dbsetEntry = DatabaseSetEntry.builder().db_Id(1L).build();
        Assert.assertTrue(databaseSetEntryService.isNotExistByName(dbsetEntry));
    }

}
