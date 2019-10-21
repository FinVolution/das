package com.ppdai.platform.das.codegen.controller;

import com.alibaba.fastjson.JSONObject;
import com.ppdai.platform.das.codegen.common.interceptor.CommStatusInterceptor;
import com.ppdai.platform.das.codegen.common.interceptor.permissions.UserLoginInterceptor;
import com.ppdai.platform.das.codegen.constant.Message;
import com.ppdai.platform.das.codegen.dao.TableEntityDao;
import com.ppdai.platform.das.codegen.dto.entry.das.TaskTable;
import com.ppdai.platform.das.codegen.dto.model.Paging;
import com.ppdai.platform.das.codegen.service.PermissionService;
import com.ppdai.platform.das.codegen.service.TableEntityService;
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
@WebMvcTest(TableEntityController.class)
public class TableEntityControllerTest {

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
        requestJson = JSONObject.toJSONString(TaskTable.builder().id(1L).dbsetName("name").build());
    }

    @Test
    public void list() throws Exception {
        Paging<TaskTable> paging = new Paging<>();
        paging.setData(new TaskTable());
        String requestJson = JSONObject.toJSONString(paging);
        mockMvc.perform(MockMvcRequestBuilders.post("/tableEntity/list")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(requestJson)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void add() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/tableEntity/add")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(requestJson)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void update() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/tableEntity/update")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(requestJson)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void delete() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/tableEntity/delete")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(requestJson)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }
}
