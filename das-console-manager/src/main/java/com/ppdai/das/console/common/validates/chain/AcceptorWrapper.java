package com.ppdai.das.console.common.validates.chain;

import com.ppdai.das.console.dto.model.ServiceResult;
import lombok.Data;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wangliang on 2017/6/22.
 */
@Data
public class AcceptorWrapper {
    private boolean isAcceptor;
    private Acceptor acceptor;
    private Executer<ServiceResult> executer;
    private String msg;
    private Map<String, String> detail;

    public AcceptorWrapper(Executer executer) {
        this.isAcceptor = false;
        this.executer = executer;
    }

    public AcceptorWrapper(Acceptor acceptor, String msg) {
        this(acceptor, msg, new HashMap<>());
    }

    public AcceptorWrapper(Acceptor acceptor, String msg, Map<String, String> detail) {
        this.isAcceptor = true;
        this.acceptor = acceptor;
        this.msg = msg;
        this.detail = detail;
    }

    public boolean accept() throws SQLException {
        return acceptor.accept();
    }

    public ServiceResult execute() throws SQLException {
        return executer.execute();
    }

}
