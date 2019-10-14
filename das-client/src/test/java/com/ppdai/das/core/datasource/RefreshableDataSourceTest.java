package com.ppdai.das.core.datasource;

import com.ppdai.das.client.DasClient;
import com.ppdai.das.client.DasClientFactory;
import com.ppdai.das.client.Person;
import com.ppdai.das.client.SqlBuilder;
import com.ppdai.das.core.configure.DataSourceConfigure;
import com.ppdai.das.core.configure.DataSourceConfigureChangeEvent;
import com.ppdai.das.core.configure.DataSourceConfigureLocatorManager;
import org.apache.commons.lang.reflect.FieldUtils;
import org.junit.Test;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

public class RefreshableDataSourceTest {

    @Test
    public void test() throws SQLException, IllegalAccessException {
        DasClient client = DasClientFactory.getClient("MySqlSimple");
        SqlBuilder sqlBuilder = SqlBuilder.selectCount().from(Person.PERSON).intoObject();
        Number before = client.queryObject(sqlBuilder);

        DataSourceConfigure oldConfig = DataSourceConfigureLocatorManager.getInstance().getDataSourceConfigure("dal_shard_0");
        DataSourceConfigure newConfig = new DataSourceConfigure(oldConfig.getName(), oldConfig.getProperties());
        DataSourceConfigureChangeEvent event = new DataSourceConfigureChangeEvent("testEvent", newConfig, oldConfig);

        ConcurrentHashMap<String, DataSource> dss = (ConcurrentHashMap<String, DataSource>) FieldUtils.readStaticField(DataSourceLocator.class, "cache", true);
        RefreshableDataSource dataSource = (RefreshableDataSource) dss.get("dal_shard_0");
        SingleDataSource oldSingleDataSource = ((AtomicReference<SingleDataSource>)FieldUtils.readField(dataSource, "dataSourceReference", true)).get();
        dataSource.configChanged(event);
        Number after = client.queryObject(sqlBuilder);

        //verify datasource changed
        assertNotSame(oldSingleDataSource, ((AtomicReference<SingleDataSource>)FieldUtils.readField(dataSource, "dataSourceReference", true)).get());
        //verify new datasource work fine
        assertEquals(before, after);
    }
}
