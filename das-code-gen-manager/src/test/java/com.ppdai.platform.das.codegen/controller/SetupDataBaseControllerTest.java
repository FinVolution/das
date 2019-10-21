package com.ppdai.platform.das.codegen.controller;


import com.alibaba.fastjson.JSONObject;
import com.ppdai.platform.das.codegen.common.interceptor.CommStatusInterceptor;
import com.ppdai.platform.das.codegen.common.interceptor.permissions.UserLoginInterceptor;
import com.ppdai.platform.das.codegen.constant.Consts;
import com.ppdai.platform.das.codegen.dto.model.ConnectionRequest;
import com.ppdai.platform.das.codegen.dto.model.InitDbUserRequset;
import com.ppdai.platform.das.codegen.service.PermissionService;
import com.ppdai.platform.das.codegen.service.SetupDataBaseService;
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
@WebMvcTest(SetupDataBaseController.class)
public class SetupDataBaseControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private UserLoginInterceptor userLoginInterceptor;

    @MockBean
    private CommStatusInterceptor commStatusInterceptor;

    @MockBean
    private PermissionService permissionService;

    @MockBean
    private SetupDataBaseService setupDataBaseService;

    @MockBean
    private Consts consts;

    @Autowired
    private SetupDataBaseService setupDBService;

    private MockMvc mockMvc;
    private String requestJson;

    @Before
    public void setupMockMvc() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build(); //初始化MockMvc对象
        requestJson = JSONObject.toJSONString(ConnectionRequest.builder().db_user("name").build());
    }

    @Test
    public void setupDbCheck() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/setupDb/setupDbCheck")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void connectionTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/setupDb/connectionTest")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(requestJson)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void tableConsistentCheck() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/setupDb/tableConsistentCheck")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(requestJson)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void initializeDb() throws Exception {
        requestJson = JSONObject.toJSONString(InitDbUserRequset.builder().adminName("name").build());
        mockMvc.perform(MockMvcRequestBuilders.post("/setupDb/initializeDb")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(requestJson)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

}
