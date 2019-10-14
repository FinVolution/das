package com.ppdai.das.client.delegate;

import java.sql.SQLException;

import com.ppdai.das.client.CallableTransaction;
import com.ppdai.das.client.Transaction;

public class CallableTransactionAdapter implements CallableTransaction<Object>{
    private Transaction transaction;
    public CallableTransactionAdapter(Transaction transaction) {
        this.transaction = transaction;
    }
    @Override
    public Object execute() throws SQLException {
        transaction.execute();
        return null;
    }

}
