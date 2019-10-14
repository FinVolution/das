package com.ppdai.das.core.task;

import java.sql.SQLException;
import java.util.List;

import com.ppdai.das.client.CallableTransaction;
import com.ppdai.das.client.Hints;
import com.ppdai.das.client.Parameter;
import com.ppdai.das.core.DalClient;
import com.ppdai.das.core.DalCommand;


public class TransactionTask<T> implements SqlBuilderTask<T>{

    @Override
    public T execute(DalClient client, StatementConditionProvider provider, List<Parameter> parameters, Hints hints) throws SQLException {
        DalCommandWrappe<T> cmd = new DalCommandWrappe<>(provider.getRawRequest());

        client.execute(cmd, hints);

        return cmd.result;
    }

    private class DalCommandWrappe<K> implements DalCommand {
        private CallableTransaction<K> transaction;
        private K result;

        DalCommandWrappe(CallableTransaction<K> transaction) {
            this.transaction = transaction;
        }

        @Override
        public boolean execute(DalClient client) throws SQLException {
            result = transaction.execute();
            return false;
        }
    }
}
