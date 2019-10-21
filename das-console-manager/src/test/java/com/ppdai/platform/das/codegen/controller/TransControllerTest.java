package com.ppdai.platform.das.codegen.controller;

import com.google.common.io.Resources;
import com.ppdai.platform.das.codegen.dto.model.ServiceResult;
import com.ppdai.platform.das.codegen.dto.model.TransRequest;
import org.junit.Assert;
import org.junit.Test;

import java.net.URL;
import java.nio.charset.StandardCharsets;


public class TransControllerTest {

    @Test
    public void getConvertCode() throws Exception{
        TransController controller = new TransController();
        TransRequest transRequest = new TransRequest();
        URL url = TransToDasControllerTest.class.getResource("/mybatisMapperTest.xml");
        transRequest.setXmlContent(Resources.toString(url, StandardCharsets.UTF_8));
        ServiceResult<String> result = controller.convert(transRequest, null, null);
        System.out.println(result.getMsg());
        URL url2 = TransToDasControllerTest.class.getResource("/expectedMybatisCode");
        String expected = Resources.toString(url2, StandardCharsets.UTF_8);
        Assert.assertEquals(expected, result.getMsg());
    }

}
