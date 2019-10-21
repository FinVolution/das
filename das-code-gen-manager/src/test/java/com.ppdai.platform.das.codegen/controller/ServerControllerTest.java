package com.ppdai.platform.das.codegen.controller;

import com.alibaba.fastjson.JSONObject;
import com.ppdai.platform.das.codegen.common.interceptor.CommStatusInterceptor;
import com.ppdai.platform.das.codegen.common.interceptor.permissions.UserLoginInterceptor;
import com.ppdai.platform.das.codegen.constant.Message;
import com.ppdai.platform.das.codegen.dao.ServerConfigDao;
import com.ppdai.platform.das.codegen.dao.ServerDao;
import com.ppdai.platform.das.codegen.dto.entry.das.Server;
import com.ppdai.platform.das.codegen.dto.model.Paging;
import com.ppdai.platform.das.codegen.service.PermissionService;
import com.ppdai.platform.das.codegen.service.ServerConfigService;
import com.ppdai.platform.das.codegen.service.ServerService;
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
@WebMvcTest(ServerController.class)
public class ServerControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private UserLoginInterceptor userLoginInterceptor;

    @MockBean
    private CommStatusInterceptor commStatusInterceptor;

    @MockBean
    private PermissionService permissionService;

    @MockBean
    private Message message;

    @MockBean
    private ServerService serverService;

    @MockBean
    private ServerDao serverDao;

    @MockBean
    private ServerConfigService serverConfigService;

    @MockBean
    private ServerConfigDao serverConfigDao;

    private MockMvc mockMvc;
    private String requestJson;

    @Before
    public void setupMockMvc() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build(); //初始化MockMvc对象
        requestJson = JSONObject.toJSONString(Server.builder().id(1L).ip("127.0.0.1").build());
    }

    @Test
    public void loadPageList() throws Exception {
        Paging<Server> paging = new Paging<>();
        paging.setData(new Server());
        String requestJson = JSONObject.toJSONString(paging);
        mockMvc.perform(MockMvcRequestBuilders.post("/server/list")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(requestJson)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void add() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/server/add")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(requestJson)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void update() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/server/update")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(requestJson)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void delete() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/server/delete")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(requestJson)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void sync() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/server/sync")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .param("id", "1")
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void check() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/server/data")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .param("id", "1")
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }
}
