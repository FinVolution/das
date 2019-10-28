package com.ppdai.das.console.common.utils;

import com.ppdai.das.console.dto.model.ServiceResult;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

@Slf4j
public class HttpServletUtil {

    public static void returnErrorResponse(HttpServletResponse response, ServiceResult result) throws IOException {
        OutputStream out = null;
        try {
            response.setCharacterEncoding("utf-8");
            response.setContentType("text/json");
            out = response.getOutputStream();
            out.write(JsonUtils.objectToJson(result).getBytes("utf-8"));
            out.flush();
        } catch (IOException ie) {
            log.error(StringUtil.getMessage(ie), ie);
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }
}
