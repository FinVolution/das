package com.ppdai.platform.das.codegen.controller;

import com.alibaba.fastjson.JSONObject;
import com.ppdai.platform.das.codegen.common.interceptor.CommStatusInterceptor;
import com.ppdai.platform.das.codegen.common.interceptor.permissions.UserLoginInterceptor;
import com.ppdai.platform.das.codegen.constant.Consts;
import com.ppdai.platform.das.codegen.dao.*;
import com.ppdai.platform.das.codegen.dto.model.GenerateCodeModel;
import com.ppdai.platform.das.codegen.service.CodeService;
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
@WebMvcTest(CodeController.class)
public class CodeControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private UserLoginInterceptor userLoginInterceptor;

    @MockBean
    private CommStatusInterceptor commStatusInterceptor;

    private MockMvc mockMvc;

    @MockBean
    private CodeService codeService;

    @MockBean
    private LoginUserDao loginUserDao;

    @MockBean
    private UserGroupDao userGroupDao;

    @MockBean
    private ProjectDao projectDao;

    @MockBean
    private Consts consts;

    @MockBean
    private DaoBySqlBuilder daoBySqlBuilder;

    @MockBean
    private TaskSqlDao daoByFreeSql;

    @Before
    public void setupMockMvc() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build(); //初始化MockMvc对象
    }

    @Test
    public void countProject() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/code/count?projectId=1001")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void generateProject() throws Exception {
        String requestJson = JSONObject.toJSONString(GenerateCodeModel.builder().projectId(1L).build());
        mockMvc.perform(MockMvcRequestBuilders.get("/code/generate")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(requestJson)
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void getFiles() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/code/files")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .param("projectId", "1")
                .param("name", "tom")
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void getFileContent() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/code/content")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .param("projectId", "1")
                .param("name", "tom")
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void download() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/code/download")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .param("projectId", "1")
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void clearFiles() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/code/clearFiles")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .param("projectId", "1")
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }
}
