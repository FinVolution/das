package com.ppdai.das.console.controller;


import com.alibaba.fastjson.JSONObject;
import com.ppdai.das.console.common.interceptor.CommStatusInterceptor;
import com.ppdai.das.console.common.interceptor.permissions.UserLoginInterceptor;
import com.ppdai.das.console.constant.Message;
import com.ppdai.das.console.controller.DatabaseSetEntryController;
import com.ppdai.das.console.dao.DataBaseDao;
import com.ppdai.das.console.dao.DataBaseSetEntryDao;
import com.ppdai.das.console.dao.DatabaseSetDao;
import com.ppdai.das.console.dto.entry.das.DatabaseSetEntry;
import com.ppdai.das.console.dto.entry.das.DataBaseInfo;
import com.ppdai.das.console.dto.model.Paging;
import com.ppdai.das.console.dto.view.DatabaseSetEntryView;
import com.ppdai.das.console.service.DatabaseSetEntryService;
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
@WebMvcTest(DatabaseSetEntryController.class)
public class DatabaseSetEntryControllerTest {

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
    private DatabaseSetDao databaseSetDao;

    @MockBean
    private DatabaseSetService databaseSetService;

    @MockBean
    private DataBaseSetEntryDao dataBaseSetEntryDao;

    @MockBean
    private DatabaseSetEntryService databaseSetEntryService;


    private MockMvc mockMvc;

    @Before
    public void setupMockMvc() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build(); //初始化MockMvc对象
    }

    @Test
    public void list() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/groupdbSetEntry//{dbsetId}/list", 1)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void loadPageList() throws Exception {
        Paging<DatabaseSetEntryView> paging = new Paging<>();
        paging.setData(new DatabaseSetEntryView());
        String requestJson = JSONObject.toJSONString(paging);
        mockMvc.perform(MockMvcRequestBuilders.post("/groupdbSetEntry/list")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(requestJson)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void add() throws Exception {
        String requestJson = JSONObject.toJSONString(DatabaseSetEntry.builder().id(1L).name("name").build());
        mockMvc.perform(MockMvcRequestBuilders.get("/groupdbSetEntry/add")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(requestJson)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void update() throws Exception {
        String requestJson = JSONObject.toJSONString(DatabaseSetEntry.builder().id(1L).name("name").build());
        mockMvc.perform(MockMvcRequestBuilders.put("/groupdbSetEntry/update")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(requestJson)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void delete() throws Exception {
        String requestJson = JSONObject.toJSONString(DatabaseSetEntry.builder().id(1L).name("name").build());
        mockMvc.perform(MockMvcRequestBuilders.delete("/groupdbSetEntry/delete")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(requestJson)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void sync() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/groupdbSetEntry/sync")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .param("id", "1")
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void check() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/groupdbSetEntry/data")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .param("id", "1")
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void syncdb() throws Exception {
        String requestJson = JSONObject.toJSONString(DataBaseInfo.builder().id(1L).dbname("name").build());
        mockMvc.perform(MockMvcRequestBuilders.post("/groupdbSetEntry/syncdb")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(requestJson)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }
}
