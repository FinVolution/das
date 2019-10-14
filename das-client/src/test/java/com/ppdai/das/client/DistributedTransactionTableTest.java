package com.ppdai.das.client;

import static com.ppdai.das.client.SqlBuilder.selectAllFrom;
import static com.ppdai.das.core.enums.DatabaseCategory.MySql;
import static com.ppdai.das.core.enums.DatabaseCategory.SqlServer;
import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.ppdai.das.client.DbSetupUtil.DbSetuper;
import com.ppdai.das.client.Person.PersonDefinition;

@RunWith(Parameterized.class)
public class DistributedTransactionTableTest {

    private static final int DB_MODE = 2;
    private static final int TABLE_MODE = 4;
    private final static String TABLE_NAME = "person";

    private DataPreparer preparer;

    private DasClient dao;
    private DbSetuper setuper;
    private static PersonDefinition p = Person.PERSON;

    @Parameters
    public static Collection data() throws SQLException {
        return Arrays.asList(new Object[][]{
            {new DasClientTest(MySql)},
         /*   {new DasClientTest(SqlServer)},*/
            
            {DasClientShardByTableTest.of(MySql)},
      /*      {DasClientShardByTableTest.of(SqlServer)},*/
        });
    }

    public DistributedTransactionTableTest(DataPreparer preparer) throws SQLException {
        this.preparer = preparer;
        dao = DasClientFactory.getClient(preparer.getDbName());
    }
    
    public Hints hints() {
        return new Hints();
    }
    
    @Before
    public void setup() throws Exception {
        preparer.setup();
    }
    
    @After
    public void tearDown() throws Exception {
        preparer.tearDown();
    }
    
    @Test
    public void testBatchWrongLogicDb() throws Exception {
        PersonDefinition p = Person.PERSON;
        SqlBuilder builder = selectAllFrom(p).where().allOf(p.CountryID.eq(0), p.CityID.eq(0)).orderBy(p.PeopleID.asc()).into(Person.class);
        List<Person> plist = dao.query(builder);
        
        try {
            DasClientFactory.getClient(preparer.getDbName(MySql)).execute(() -> {
                DasClient c2 = DasClientFactory.getClient(preparer.getDbName(SqlServer));
                c2.batchDelete(plist);
            }, new Hints().inShard(0));
            fail();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
