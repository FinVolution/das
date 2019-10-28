package com.ppdai.das.console.common.validates.chain;

import java.sql.SQLException;

/**
 * Created by wangliang on 2017/6/22.
 */
public interface Acceptor  {
    boolean accept() throws SQLException;

}
