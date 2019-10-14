package com.ppdai.das.core.datasource;

public class DefaultConnectionListener extends AbstractConnectionListener implements ConnectionListener {

    @Override
    public int getOrder() {
        return LOWEST_PRECEDENCE;
    }

}
