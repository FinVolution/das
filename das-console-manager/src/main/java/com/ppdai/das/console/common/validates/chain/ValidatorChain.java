package com.ppdai.das.console.common.validates.chain;

import com.ppdai.das.console.dto.model.ServiceResult;
import org.apache.commons.lang.StringUtils;
import org.springframework.validation.Errors;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 对一系列串行校验进行封装，避免一大堆if条件判断
 * 注：
 * 此方法addAssert(final boolean b, String msg)不能达到延迟运算的效果
 * 建议逻辑使用SerializeValidator addAssert(Acceptor acceptor, String msg)，以达到懒加载的效果
 */
public class ValidatorChain {

    private List<AcceptorWrapper> acceptorWrappers = new ArrayList<>();

    public static ValidatorChain newInstance() {
        return new ValidatorChain();
    }

    public ValidatorChain addAssert(final boolean b, String msg) {
        acceptorWrappers.add(new AcceptorWrapper(() -> b, msg));
        return this;
    }

    public ValidatorChain addAssert(final boolean b, String msg, Map<String, String> detail) {
        acceptorWrappers.add(new AcceptorWrapper(() -> b, msg, detail));
        return this;
    }

    public ValidatorChain addAssert(Acceptor acceptor, String msg) {
        acceptorWrappers.add(new AcceptorWrapper(acceptor, msg));
        return this;
    }

    public ValidatorChain addAssert(Executer executer) {
        acceptorWrappers.add(new AcceptorWrapper(executer));
        return this;
    }

    public ValidatorChain addAssert(Acceptor acceptor, String msg, Map<String, String> detail) {
        acceptorWrappers.add(new AcceptorWrapper(acceptor, msg, detail));
        return this;
    }

    public ValidateResult validate() throws SQLException {
        ValidateResult success = ValidateResult.validResult();
        if (acceptorWrappers == null || acceptorWrappers.isEmpty()) return success;
        for (AcceptorWrapper acceptorWrapper : acceptorWrappers) {
            if(acceptorWrapper.isAcceptor()){
                if (!acceptorWrapper.accept()) {
                    return ValidateResult.invalidResult(acceptorWrapper.getMsg(), acceptorWrapper.getDetail());
                }
            }else{
                ServiceResult sr = acceptorWrapper.execute();
                if (sr.getCode() == ServiceResult.ERROR) {
                    return ValidateResult.invalidResult(sr.getMsg().toString(), acceptorWrapper.getDetail());
                }
            }
        }
        return success;
    }

    /**
     * 校验 Controller 层数据 @Validated 的结果
     */
    public ValidatorChain controllerValidate(Errors... errorsList) {
        for (Errors errors : errorsList) {
            addAssert(!errors.hasErrors(), errors.hasErrors() ? errors.getFieldError().getDefaultMessage() : StringUtils.EMPTY);
        }
        return this;
    }
}
