package com.ppdai.platform.das.codegen.controller;

import com.alibaba.fastjson.JSONObject;
import com.ppdai.platform.das.codegen.common.interceptor.CommStatusInterceptor;
import com.ppdai.platform.das.codegen.common.interceptor.permissions.UserLoginInterceptor;
import com.ppdai.platform.das.codegen.constant.Message;
import com.ppdai.platform.das.codegen.dao.ServerGroupDao;
import com.ppdai.platform.das.codegen.dto.entry.das.ServerGroup;
import com.ppdai.platform.das.codegen.dto.model.Paging;
import com.ppdai.platform.das.codegen.service.PermissionService;
import com.ppdai.platform.das.codegen.service.ServerGroupService;
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
@WebMvcTest(ServerGroupController.class)
public class ServerGroupControllerTest {

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
    private ServerGroupService serverGroupService;

    @MockBean
    private ServerGroupDao serverGroupDaoOld;

    private MockMvc mockMvc;
    private String requestJson;

    @Before
    public void setupMockMvc() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build(); //初始化MockMvc对象
        requestJson = JSONObject.toJSONString(ServerGroup.builder().id(1L).name("name").build());
    }

    @Test
    public void loadAllServerGroups() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/serverGroup/loadAllServerGroups")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void serversNoGroup() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/serverGroup/serversNoGroup")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .param("appGroupId", "1")
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void loadPageList() throws Exception {
        Paging<ServerGroup> paging = new Paging<>();
        paging.setData(new ServerGroup());
        String requestJson = JSONObject.toJSONString(paging);
        mockMvc.perform(MockMvcRequestBuilders.post("/serverGroup/list")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(requestJson)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void add() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/serverGroup/add")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(requestJson)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void update() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/serverGroup/update")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(requestJson)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void delete() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/serverGroup/delete")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(requestJson)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void sync() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/serverGroup/sync")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .param("id", "1")
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void check() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/serverGroup/data")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .param("id", "1")
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }
}
