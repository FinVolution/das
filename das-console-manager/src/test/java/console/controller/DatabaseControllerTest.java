package com.ppdai.platform.das.console.controller;


import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.ppdai.platform.das.console.common.interceptor.CommStatusInterceptor;
import com.ppdai.platform.das.console.common.interceptor.permissions.UserLoginInterceptor;
import com.ppdai.platform.das.console.constant.Message;
import com.ppdai.platform.das.console.dao.DataBaseDao;
import com.ppdai.platform.das.console.dao.GroupDao;
import com.ppdai.platform.das.console.dto.entry.das.DataBaseInfo;
import com.ppdai.platform.das.console.dto.model.Paging;
import com.ppdai.platform.das.console.dto.model.SqlValidateRequest;
import com.ppdai.platform.das.console.service.DatabaseService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

@RunWith(SpringRunner.class)
@WebMvcTest(DatabaseController.class)
public class DatabaseControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private UserLoginInterceptor userLoginInterceptor;

    @MockBean
    private CommStatusInterceptor commStatusInterceptor;

    @MockBean
    private Message message;

    @MockBean
    private DataBaseDao dataBaseDao;

    @MockBean
    private DatabaseService databaseService;

    @MockBean
    private GroupDao groupDao;


    private MockMvc mockMvc;
    
    @Before
    public void setupMockMvc() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build(); //初始化MockMvc对象
    }

    @Test
    public void getDBListByGroupId() throws Exception {
        Paging<DataBaseInfo> paging = new Paging<>();
        paging.setData(new DataBaseInfo());
        String requestJson = JSONObject.toJSONString(paging);
        mockMvc.perform(MockMvcRequestBuilders.post("/db/list")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(requestJson)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void add() throws Exception {
        String requestJson = JSONObject.toJSONString(DataBaseInfo.builder().id(1L).dbname("name").build());
        mockMvc.perform(MockMvcRequestBuilders.get("/db/add")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(requestJson)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void addDbs() throws Exception {
        List<DataBaseInfo> list = Lists.newArrayList(DataBaseInfo.builder().id(1L).dbname("name").build());
        String requestJson = JSONObject.toJSONString(list);
        mockMvc.perform(MockMvcRequestBuilders.post("/db/addDbs")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(requestJson)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void update() throws Exception {
        String requestJson = JSONObject.toJSONString(DataBaseInfo.builder().id(1L).dbname("name").build());
        mockMvc.perform(MockMvcRequestBuilders.put("/db/update")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(requestJson)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void delete() throws Exception {
        String requestJson = JSONObject.toJSONString(DataBaseInfo.builder().id(1L).dbname("name").build());
        mockMvc.perform(MockMvcRequestBuilders.delete("/db/delete")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(requestJson)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void getDBCatalogs() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/db/catalogs")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void validateSQL() throws Exception {
        String requestJson = JSONObject.toJSONString(SqlValidateRequest.builder().sql_content("select * from project").build());
        mockMvc.perform(MockMvcRequestBuilders.post("/db/sqlValidate")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(requestJson)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void sync() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/db/sync")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .param("id", "1")
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void check() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/db/data")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .param("id", "1")
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void config() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/db/config")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .param("name", "name")
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void syncdb() throws Exception {
        String requestJson = JSONObject.toJSONString(DataBaseInfo.builder().id(1L).dbname("name").build());
        mockMvc.perform(MockMvcRequestBuilders.post("/db/syncdb")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(requestJson)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }
}
