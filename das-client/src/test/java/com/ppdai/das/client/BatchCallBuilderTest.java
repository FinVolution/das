package com.ppdai.das.client;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.sql.JDBCType;
import java.sql.SQLException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ppdai.das.client.BatchCallBuilder;
import com.ppdai.das.client.BatchUpdateBuilder;
import com.ppdai.das.client.DasClient;
import com.ppdai.das.client.DasClientFactory;
import com.ppdai.das.client.Hints;

public class BatchCallBuilderTest {
    public final static String SP_WITHOUT_OUT_PARAM = "SP_WITHOUT_OUT_PARAM";
    public final static String SP_WITH_OUT_PARAM = "SP_WITH_OUT_PARAM";
    public final static String SP_WITH_IN_OUT_PARAM = "SP_WITH_IN_OUT_PARAM";
    public final static String SP_WITH_INTERMEDIATE_RESULT = "SP_WITH_INTERMEDIATE_RESULT";
    private static final String CREATE_SP_WITHOUT_OUT_PARAM = "CREATE PROCEDURE " + SP_WITHOUT_OUT_PARAM + "("
            + "v_id int,"
            + "v_cityID int,"
            + "v_countryID int,"
            + "v_name VARCHAR(64)) "
            + "BEGIN INSERT INTO person"
            + "(peopleid, cityID, countryID, name) "
            + "VALUES(v_id, v_cityID, v_countryID, v_name);"
            + "END";

    //Has out parameters store procedure
    private static final String CREATE_SP_WITH_OUT_PARAM = "CREATE PROCEDURE " + SP_WITH_OUT_PARAM + "("
            + "v_id int,"
            + "out count int)"
            + "BEGIN DELETE FROM person WHERE peopleid=v_id;"
            + "SELECT COUNT(*) INTO count from person;"
            + "END";

    //Has in-out parameters store procedure
    private static final String CREATE_SP_WITH_IN_OUT_PARAM = "CREATE PROCEDURE " + SP_WITH_IN_OUT_PARAM + "("
            + "v_id int,"
            + "v_cityID int,"
            + "v_countryID int,"
            + "INOUT v_name VARCHAR(64))"
            + "BEGIN UPDATE person "
            + "SET cityID = v_cityID, countryID=v_countryID, name=v_name "
            + "WHERE peopleid=v_id;"
            + "SELECT 'output' INTO v_name;"
            + "END";

    //auto get all result parameters store procedure
    private static final String CREATE_SP_WITH_INTERMEDIATE_RESULT = "CREATE PROCEDURE " + SP_WITH_INTERMEDIATE_RESULT + "("
            + "v_id int,"
            + "v_quantity int,"
            + "v_type smallint,"
            + "INOUT v_address VARCHAR(64))"
            + "BEGIN UPDATE dal_client_test "
            + "SET quantity = v_quantity, type=v_type, address=v_address "
            + "WHERE id=v_id;"
            + "SELECT ROW_COUNT() AS result;"
            + "SELECT 1 AS result2;"
            + "UPDATE dal_client_test "
            + "SET `quantity` = quantity + 1, `type`=type + 1, `address`='aaa';"
            + "SELECT 'abc' AS result3, 456 AS count2;"
            + "SELECT * from dal_client_test;"
            + "SELECT 'output' INTO v_address;"
            + "END";

    private static final String DROP_SP_WITHOUT_OUT_PARAM = "DROP PROCEDURE IF EXISTS " + SP_WITHOUT_OUT_PARAM;
    private static final String DROP_SP_WITH_OUT_PARAM = "DROP PROCEDURE IF EXISTS " + SP_WITH_OUT_PARAM;
    private static final String DROP_SP_WITH_IN_OUT_PARAM = "DROP PROCEDURE IF EXISTS " + SP_WITH_IN_OUT_PARAM;
    private static final String DROP_SP_WITH_INTERMEDIATE_RESULT = "DROP PROCEDURE IF EXISTS " + SP_WITH_INTERMEDIATE_RESULT;

    private final static String DATABASE_LOGIC_NAME = "MySqlConditionDbShard";
    private static final int DB_MODE = 2;
    private static DasClient dao;

    @BeforeClass
    public static void setupDataBase() throws SQLException {
        dao = DasClientFactory.getClient(DATABASE_LOGIC_NAME);
        String[] sqls = new String[]{
                DROP_SP_WITHOUT_OUT_PARAM, CREATE_SP_WITHOUT_OUT_PARAM,
                DROP_SP_WITH_OUT_PARAM, CREATE_SP_WITH_OUT_PARAM,
                DROP_SP_WITH_IN_OUT_PARAM, CREATE_SP_WITH_IN_OUT_PARAM,
                DROP_SP_WITH_INTERMEDIATE_RESULT, CREATE_SP_WITH_INTERMEDIATE_RESULT};
        BatchUpdateBuilder b = new BatchUpdateBuilder(sqls);
        b.hints().inShard(0);
        dao.batchUpdate(b);
        b.hints().inShard(1);
        dao.batchUpdate(b);
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        String[] sqls = new String[]{
                DROP_SP_WITHOUT_OUT_PARAM,
                DROP_SP_WITH_OUT_PARAM,
                DROP_SP_WITH_IN_OUT_PARAM,
                DROP_SP_WITH_INTERMEDIATE_RESULT};
        BatchUpdateBuilder b = new BatchUpdateBuilder(sqls);
        b.hints().inShard(0);
        dao.batchUpdate(b);
        b.hints().inShard(1);
        dao.batchUpdate(b);
    }

    @Before
    public void setup() throws Exception {
        for (int i = 0; i < DB_MODE; i++) {
            String[] statements = new String[4];
            for (int j = 0; j < 4; j++)
                statements[j] = String.format("INSERT INTO person(PeopleID, Name, CountryID, CityID, ProvinceID) VALUES(%d, 'test', %d, %d, 1)", j + 1, i, j);
            BatchUpdateBuilder builder = new BatchUpdateBuilder(statements);
            builder.hints().inShard(i);
            dao.batchUpdate(builder);
        }
    }

    @After
    public void tearDown() throws SQLException {
        for (int i = 0; i < DB_MODE; i++) {
            String[] statements = new String[1];
            statements[0] = "DELETE FROM person";
            BatchUpdateBuilder builder = new BatchUpdateBuilder(statements);
            builder.hints().inShard(i);
            dao.batchUpdate(builder);
        }
    }

    @Test
    public void testCall() throws Exception {
        for (int i = 0; i < DB_MODE; i++) {
            BatchCallBuilder cb = new BatchCallBuilder(SP_WITHOUT_OUT_PARAM);
            cb.registerInput("v_id", JDBCType.INTEGER);
            cb.registerInput("v_cityID", JDBCType.INTEGER);
            cb.registerInput("v_countryID", JDBCType.INTEGER);
            cb.registerInput("v_name", JDBCType.VARCHAR);
            cb.hints().inShard(i);

            cb.addBatch(7, 7, 7, "777");
            cb.addBatch(17, 17, 17, "1777");
            cb.addBatch(27, 27, 27, "2777");
            int[] ret = dao.batchCall(cb);
            assertArrayEquals(new int[]{1, 1, 1}, ret);

            Person p = new Person();
            p.setPeopleID(7);
            p = dao.queryByPk(p, Hints.hints().inShard(i));
            assertEquals("777", p.getName());

            p = new Person();
            p.setPeopleID(17);
            p = dao.queryByPk(p, Hints.hints().inShard(i));
            assertEquals("1777", p.getName());

            p = new Person();
            p.setPeopleID(27);
            p = dao.queryByPk(p, Hints.hints().inShard(i));
            assertEquals("2777", p.getName());
        }
    }

    @Test
    public void testCallWithOutParam() throws Exception {
        for (int i = 0; i < DB_MODE; i++) {
            BatchCallBuilder cb = new BatchCallBuilder(SP_WITH_OUT_PARAM);
            cb.registerInput("v_id", JDBCType.INTEGER);
            cb.registerOutput("count", JDBCType.INTEGER);
            cb.addBatch(1);
            cb.addBatch(2);
            cb.addBatch(3);

            cb.hints().inShard(i);

            int[] ret = dao.batchCall(cb);
            assertArrayEquals(new int[]{1, 1, 1}, ret);

            Person p = new Person();
            p.setPeopleID(1);
            assertNull(dao.queryByPk(p, Hints.hints().inShard(i)));

            p = new Person();
            p.setPeopleID(2);
            assertNull(dao.queryByPk(p, Hints.hints().inShard(i)));

            p = new Person();
            p.setPeopleID(3);
            assertNull(dao.queryByPk(p, Hints.hints().inShard(i)));
        }
    }

    @Test
    public void testCallWithInputOutputParam() throws Exception {
        for (int i = 0; i < DB_MODE; i++) {
            BatchCallBuilder cb = new BatchCallBuilder(SP_WITH_IN_OUT_PARAM);
            cb.registerInput("v_id", JDBCType.INTEGER);
            cb.registerInput("v_cityID", JDBCType.INTEGER);
            cb.registerInput("v_countryID", JDBCType.INTEGER);
            cb.registerInputOutput("v_name", JDBCType.VARCHAR);

            cb.addBatch(1, 1, 3, "xxx");
            cb.addBatch(2, 2, 2, "yyy");
            cb.addBatch(3, 3, 1, "zzz");

            cb.hints().inShard(i);

            int[] ret = dao.batchCall(cb);
            assertArrayEquals(new int[]{1, 1, 1}, ret);

//            String name = cb.getOutput("v_name");
//            assertEquals("output",  name);
            // The inout parameter may pollute consequent call


            Person p = new Person();
            p.setPeopleID(1);
            p = dao.queryByPk(p, Hints.hints().inShard(i));
            assertEquals(1, p.getCityID().intValue());
            assertEquals(3, p.getCountryID().intValue());

            p = new Person();
            p.setPeopleID(2);
            p = dao.queryByPk(p, Hints.hints().inShard(i));
            assertEquals(2, p.getCityID().intValue());
            assertEquals(2, p.getCountryID().intValue());

            p = new Person();
            p.setPeopleID(3);
            p = dao.queryByPk(p, Hints.hints().inShard(i));
            assertEquals(3, p.getCityID().intValue());
            assertEquals(1, p.getCountryID().intValue());
        }
    }
}
