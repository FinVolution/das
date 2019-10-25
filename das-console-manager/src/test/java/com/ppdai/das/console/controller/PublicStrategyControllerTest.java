package com.ppdai.das.console.controller;

import com.alibaba.fastjson.JSONObject;
import com.ppdai.das.console.common.interceptor.CommStatusInterceptor;
import com.ppdai.das.console.common.interceptor.permissions.UserLoginInterceptor;
import com.ppdai.das.console.constant.Message;
import com.ppdai.das.console.controller.PublicStrategyController;
import com.ppdai.das.console.dao.PublicStrategyDao;
import com.ppdai.das.console.dto.entry.das.PublicStrategy;
import com.ppdai.das.console.dto.model.Paging;
import com.ppdai.das.console.service.PermissionService;
import com.ppdai.das.console.service.PublicStrategyService;
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
@WebMvcTest(PublicStrategyController.class)
public class PublicStrategyControllerTest {

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
    private PublicStrategyService publicStrategyService;

    @MockBean
    private PublicStrategyDao publicStrategyDao;

    private MockMvc mockMvc;
    private String requestJson;

    @Before
    public void setupMockMvc() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build(); //初始化MockMvc对象
        requestJson = JSONObject.toJSONString(PublicStrategy.builder().id(1L).name("name").build());
    }

    @Test
    public void list() throws Exception {
        Paging<PublicStrategy> paging = new Paging<>();
        paging.setData(new PublicStrategy());
        String requestJson = JSONObject.toJSONString(paging);
        mockMvc.perform(MockMvcRequestBuilders.post("/publicStrategy/list")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(requestJson)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void list2() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/publicStrategy/list")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void add() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/publicStrategy/add")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(requestJson)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void update() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/publicStrategy/update")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(requestJson)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void delete() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/publicStrategy/delete")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(requestJson)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void sync() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/publicStrategy/sync")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .param("id", "1")
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void check() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/publicStrategy/data")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .param("id", "1")
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }
}
