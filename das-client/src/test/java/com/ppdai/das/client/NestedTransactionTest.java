package com.ppdai.das.client;

import static com.ppdai.das.client.Hints.hints;
import static com.ppdai.das.client.SqlBuilder.selectAllFrom;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
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

import com.ppdai.das.client.Person.PersonDefinition;
import com.ppdai.das.core.enums.DatabaseCategory;
import static com.ppdai.das.core.enums.DatabaseCategory.*;

@RunWith(Parameterized.class)
public class NestedTransactionTest {
    private static final boolean PASS = true;
    private static final boolean FAIL = !PASS;
    
    private final static String MYSQL_SLEEP = "select sleep(70)";
    private final static String SQLSVR_SLEEP = "waitfor delay '00:01:10.100'";

    private String sleepClause;
    private DasClient dao;
    private DataPreparer preparer;
    private static PersonDefinition p = Person.PERSON;
    private boolean callable;

    @Parameters
    public static Collection data() throws SQLException {
        return Arrays.asList(new Object[][]{
            {DasClientShardByDbTableTest.of(DatabaseCategory.MySql), true}, 
            {DasClientShardByDbTableTest.of(DatabaseCategory.SqlServer), true},
            
            {DasClientShardByDBTest.of(DatabaseCategory.MySql), true}, 
            {DasClientShardByDBTest.of(DatabaseCategory.SqlServer), true},
            
            {DasClientShardByTableTest.of(DatabaseCategory.MySql), true}, 
            {DasClientShardByTableTest.of(DatabaseCategory.SqlServer), true},
            
            {new DasClientTest(MySql), true},
            {new DasClientTest(SqlServer), true},
            //==============================================================//
            {DasClientShardByDbTableTest.of(DatabaseCategory.MySql), false}, 
            {DasClientShardByDbTableTest.of(DatabaseCategory.SqlServer), false},
            
            {DasClientShardByDBTest.of(DatabaseCategory.MySql), false}, 
            {DasClientShardByDBTest.of(DatabaseCategory.SqlServer), false},
            
            {DasClientShardByTableTest.of(DatabaseCategory.MySql), false}, 
            {DasClientShardByTableTest.of(DatabaseCategory.SqlServer), false},
            
            {new DasClientTest(MySql), false},
            {new DasClientTest(SqlServer), false},
        });
    }
    
    public NestedTransactionTest(DataPreparer preparer, boolean callable) throws SQLException {
        this.preparer = preparer;
        this.callable = callable;
        this.sleepClause = preparer.getDbCategory().equals(MySql) ? MYSQL_SLEEP : SQLSVR_SLEEP;
        dao = DasClientFactory.getClient(preparer.getDbName(preparer.dbCategory));
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
    public void testTransactionNestDeep() throws Exception {
        test(PASS, ()->{
            try {
                System.out.println("level 1");
                dao.execute(() -> {
                    System.out.println("level 2");
                    dao.execute(() -> {
                        System.out.println("level 3");
                        dao.execute(() -> {
                            System.out.println("level 4");
                        }, getHints());
                    }, getHints());
                }, getHints());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }            
        });    
    }
    
    @Test
    public void testTransactionNestClientException() {
        test(FAIL, ()->{
            try {
                System.out.println("level 1");
                dao.execute(() -> {
                    System.out.println("level 2");
                    dao.execute(() -> {
                        System.out.println("level 3");
                        dao.execute(() -> {
                            System.out.println("level 4");
                            System.out.println("Throw client side exception");
                            throw new RuntimeException();
                        }, getHints());
                    }, getHints());
                }, getHints());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }            
        });    
    }

    @Test
    public void testTransactionNestServerException() {
        test(FAIL, ()->{
            try {
                System.out.println("level 1");
                dao.execute(() -> {
                    System.out.println("level 2");
                    dao.execute(() -> {
                        System.out.println("level 3");
                        dao.execute(() -> {
                            System.out.println("level 4");
                            SqlBuilder builder = selectAllFrom(p).where("xxx").into(Person.class);
                            dao.query(builder);
                        }, getHints());
                    }, getHints());
                }, getHints());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }            
        });
    }
    
    private void test(boolean pass, Runnable task) {
        try {
            if(callable) {
                dao.execute(() -> {
                    removeRecords();
                    task.run();
                    return 1;
                }, getHints());
            }else {
                dao.execute(() -> {
                    removeRecords();
                    task.run();
                }, getHints());
            }
            if(pass)
                checkCount(0);
            else
                fail();
        } catch (Throwable e) {
            if(pass)
                fail();
            else
                checkCount(4);
        }            
    }

    private void removeRecords() throws SQLException {
        PersonDefinition p = Person.PERSON;
        dao.execute(() -> {
            SqlBuilder builder = selectAllFrom(p).where(p.PeopleID.gt(0)).orderBy(p.PeopleID.asc()).into(Person.class);
            setHints(builder.hints());
            List<Person> plist = dao.query(builder);
            
            assertEquals(4, plist.size());
            assertArrayEquals(new int[] {1, 1, 1, 1}, dao.batchDelete(plist, getHints()));
        }, getHints());
    }
    
    private void checkCount(int count)  {
        PersonDefinition p = Person.PERSON;
        try {
            dao.execute(() -> {
                SqlBuilder builder = selectAllFrom(p).where(p.PeopleID.gt(0)).orderBy(p.PeopleID.asc()).into(Person.class);
                setHints(builder.hints());
                assertEquals(count, dao.query(builder).size());
            }, getHints());
        } catch (SQLException e) {
            fail();
        }            
    }
    
    private Hints setHints(Hints hints) {
        return hints.inTableShard(1).inShard(1);
    }

    private Hints getHints() {
        return setHints(new Hints());
    }
}
