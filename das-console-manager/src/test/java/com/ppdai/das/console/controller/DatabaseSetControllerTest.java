package com.ppdai.das.console.controller;

import com.alibaba.fastjson.JSONObject;
import com.ppdai.das.console.common.interceptor.CommStatusInterceptor;
import com.ppdai.das.console.common.interceptor.permissions.UserLoginInterceptor;
import com.ppdai.das.console.constant.Message;
import com.ppdai.das.console.controller.DatabaseSetController;
import com.ppdai.das.console.dao.DatabaseSetDao;
import com.ppdai.das.console.dao.GroupDao;
import com.ppdai.das.console.dao.ProjectDao;
import com.ppdai.das.console.dto.entry.das.DatabaseSet;
import com.ppdai.das.console.dto.model.Paging;
import com.ppdai.das.console.service.DatabaseSetService;
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

@RunWith(SpringRunner.class)
@WebMvcTest(DatabaseSetController.class)
public class DatabaseSetControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private UserLoginInterceptor userLoginInterceptor;

    @MockBean
    private CommStatusInterceptor commStatusInterceptor;

    @MockBean
    private Message message;

    @MockBean
    private ProjectDao projectDao;

    @MockBean
    private GroupDao groupDao;

    @MockBean
    private DatabaseSetService databaseSetService;

    @MockBean
    private DatabaseSetDao databaseSetDao;

    private MockMvc mockMvc;

    @Before
    public void setupMockMvc() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build(); //初始化MockMvc对象
    }

    @Test
    public void lists() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/groupdbset/{groupId}/list", 1)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void list() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/groupdbset/list")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .param("projectId", "1")
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void loadPageList() throws Exception {
        Paging<DatabaseSet> paging = new Paging<>();
        paging.setData(new DatabaseSet());
        String requestJson = JSONObject.toJSONString(paging);
        mockMvc.perform(MockMvcRequestBuilders.post("/groupdbset/list")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(requestJson)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void add() throws Exception {
        String requestJson = JSONObject.toJSONString(DatabaseSet.builder().id(1L).name("name").build());
        mockMvc.perform(MockMvcRequestBuilders.get("/groupdbset/add")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(requestJson)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void update() throws Exception {
        String requestJson = JSONObject.toJSONString(DatabaseSet.builder().id(1L).name("name").build());
        mockMvc.perform(MockMvcRequestBuilders.put("/groupdbset/update")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(requestJson)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void delete() throws Exception {
        String requestJson = JSONObject.toJSONString(DatabaseSet.builder().id(1L).name("name").build());
        mockMvc.perform(MockMvcRequestBuilders.delete("/groupdbset/delete")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(requestJson)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void sync() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/groupdbset/sync")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .param("id", "1")
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void check() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/groupdbset/data")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .param("id", "1")
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void groupCheck() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/groupdbset/groupCheck")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .param("gourpId", "1")
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void config() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/groupdbset/config")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .param("appId", "123456")
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void syncdb() throws Exception {
        String requestJson = JSONObject.toJSONString(DatabaseSet.builder().id(1L).name("name").build());
        mockMvc.perform(MockMvcRequestBuilders.post("/groupdbset/syncdb")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(requestJson)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }
}
