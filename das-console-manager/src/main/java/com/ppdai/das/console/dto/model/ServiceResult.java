package com.ppdai.das.console.dto.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by wanglinag
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceResult<T> {

    public static final int ERROR = 500;
    public static final int SUCCESS = 200;
    private int code = SUCCESS;
    private T msg;

    public static ServiceResult<String> fail() {
        ServiceResult<String> serviceResult = new ServiceResult();
        serviceResult.setCode(ServiceResult.ERROR);
        serviceResult.setMsg("fail");
        return serviceResult;
    }

    public static <T> ServiceResult<T> fail(Object msg) {
        ServiceResult serviceResult = new ServiceResult();
        serviceResult.setCode(ServiceResult.ERROR);
        serviceResult.setMsg(msg);
        return serviceResult;
    }

    public static ServiceResult<String> success() {
        ServiceResult<String> serviceResult = new ServiceResult();
        serviceResult.setMsg("success");
        return serviceResult;
    }

    public static <T> ServiceResult<T> success(Object msg) {
        ServiceResult serviceResult = new ServiceResult();
        serviceResult.setMsg(msg);
        return serviceResult;
    }
}
