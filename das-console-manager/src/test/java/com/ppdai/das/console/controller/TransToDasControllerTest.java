package com.ppdai.das.console.controller;


import com.alibaba.fastjson.JSONObject;
import com.ppdai.das.console.common.interceptor.CommStatusInterceptor;
import com.ppdai.das.console.common.interceptor.permissions.UserLoginInterceptor;
import com.ppdai.das.console.constant.Message;
import com.ppdai.das.console.controller.TransController;
import com.ppdai.das.console.dao.TableEntityDao;
import com.ppdai.das.console.dto.entry.das.TaskTable;
import com.ppdai.das.console.dto.model.Paging;
import com.ppdai.das.console.dto.model.TransRequest;
import com.ppdai.das.console.service.PermissionService;
import com.ppdai.das.console.service.TableEntityService;
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
@WebMvcTest(TransController.class)
public class TransToDasControllerTest {


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
    private TableEntityDao tableEntityDao;

    @MockBean
    private TableEntityService tableEntityService;

    private MockMvc mockMvc;
    private String requestJson;

    @Before
    public void setupMockMvc() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build(); //初始化MockMvc对象
        requestJson = JSONObject.toJSONString(TransRequest.builder().xmlContent("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<Datasources><Datasource name=\"dao\" userName=\"root\" password=\"root\" connectionUrl=\"jdbc:mysql://127.0.0.1:3306/code_gen\" driverClassName=\"com.mysql.jdbc.Driver\"/></Datasources>").build());
    }

    @Test
    public void toDas() throws Exception {
        Paging<TaskTable> paging = new Paging<>();
        paging.setData(new TaskTable());
        String requestJson = JSONObject.toJSONString(paging);
        mockMvc.perform(MockMvcRequestBuilders.post("/trans/toDas")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(requestJson)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

}
