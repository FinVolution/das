package com.ppdai.das.client;

import static com.ppdai.das.core.enums.DatabaseCategory.MySql;
import static com.ppdai.das.core.enums.DatabaseCategory.SqlServer;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.util.ArrayList;
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

@RunWith(Parameterized.class)
public class LogicDeletionDaoUtilMethodsTest {
    private final static String DATABASE_LOGIC_NAME_MYSQL = "MySqlSimple";
    private final static String DATABASE_LOGIC_NAME_SQLSVR = "SqlSvrSimple";
    private final static Integer DELETED = 0;
    private final static Integer ACTIVE = 1;
    
    private DatabaseCategory dbCategory;

    private static PersonDefinition p = Person.PERSON;

    @Parameters
    public static Collection data() {
        return Arrays.asList(new Object[][]{
                {SqlServer},
                {MySql},
        });
    }
    
    public LogicDeletionDaoUtilMethodsTest(DatabaseCategory dbCategory) throws Exception {
        this.dbCategory = dbCategory;
    }
    
    private LogicDeletionDao<Person> buildDao(boolean validateInput, boolean filterOutput) throws Exception {
        DeletionFieldSupport<Person> support = new DeletionFieldSupport<>(Person.class, Person.PERSON.ProvinceID, DELETED, ACTIVE);
        return new LogicDeletionDao<>(getDbName(dbCategory), Person.class, support, validateInput, filterOutput);
    }

    public String getDbName(DatabaseCategory dbCategory) {
        return dbCategory.equals(MySql) ? DATABASE_LOGIC_NAME_MYSQL : DATABASE_LOGIC_NAME_SQLSVR;
    }
    
    @Test
    public void testIsDeleted() throws Exception {
        LogicDeletionDao<Person> dao = buildDao(true, true);
        
        Person p;
        p = new Person();
        p.setProvinceID(DELETED);
        assertTrue(dao.isDeleted(p));

        p = new Person();
        p.setProvinceID(ACTIVE);
        assertFalse(dao.isDeleted(p));
        
        p = new Person();
        //Not set flag
        assertFalse(dao.isDeleted(p));
    }
    
    @Test
    public void testFilterDeletedRecord() throws Exception {
        LogicDeletionDao<Person> dao = buildDao(true, true);
        
        Person p;
        List<Person> entities = new ArrayList<>();
        p = new Person();
        p.setProvinceID(DELETED);
        entities.add(p);

        p = new Person();
        p.setProvinceID(ACTIVE);
        entities.add(p);
        
        p = new Person();
        //Not set flag
        entities.add(p);
        
        List<Person> deleted = dao.filterDeleted(entities);
        assertEquals(1, deleted.size());
        assertTrue(dao.isDeleted(deleted.get(0)));

        assertEquals(2, entities.size());
        assertFalse(dao.isDeleted(entities.get(0)));
        assertFalse(dao.isDeleted(entities.get(1)));
    }
    
    @Test
    public void testContainsDeletedRecord() throws Exception {
        LogicDeletionDao<Person> dao = buildDao(true, true);
        
        Person p;
        List<Person> entities = new ArrayList<>();
        p = new Person();
        p.setProvinceID(ACTIVE);
        entities.add(p);
        
        p = new Person();
        //Not set flag
        entities.add(p);
        
        assertFalse(dao.containsDeleted(entities));
        
        p = new Person();
        p.setProvinceID(DELETED);
        entities.add(p);

        assertTrue(dao.containsDeleted(entities));
    }    
    
    @Test
    public void testGetActiveCondition() throws Exception {
        LogicDeletionDao<Person> dao = buildDao(true, true);
        Object[] con = dao.getActiveCondition(p);
        assertNotNull(con);
    }

    @Test
    public void testGetDeletionSupport() throws Exception {
        LogicDeletionDao<Person> dao = buildDao(true, true);
        assertNotNull(dao.getDeletionSupport());
    }

    @Test
    public void testValidateInput() throws Exception {
        LogicDeletionDao<Person> dao = buildDao(true, true);
        
        Person p;
        p = new Person();
        p.setProvinceID(ACTIVE);
        try {
            dao.validateInput(p);
        } catch (Exception e) {
            fail();
        }
        
        p = new Person();
        //Not set deletion flag
        try {
            dao.validateInput(p);
        } catch (Exception e) {
            fail();
        }
        
        p.setProvinceID(DELETED);
        try {
            dao.validateInput(p);
            fail();
        } catch (Exception e) {
        }
        
        // Test for list case
        List<Person> entities = new ArrayList<>();
        p = new Person();
        p.setProvinceID(ACTIVE);
        entities.add(p);
        
        try {
            dao.validateInput(entities);
        } catch (Exception e) {
            fail();
        }
        
        p.setProvinceID(DELETED);
        try {
            dao.validateInput(entities);
            fail();
        } catch (Exception e) {
        }
    }
//
//    @Test
//    public void testNotValidateInput() throws Exception {
//        LogicDeletionDao<Person> dao = buildDao(false, true);
//        
//        Person p;
//        p = new Person();
//        //Not set deletion flag
//        dao.validateInput(p);
//        
//        p.setProvinceID(ACTIVE);
//        dao.validateInput(p);
//        
//        p.setProvinceID(DELETED);
//        dao.validateInput(p);
//        
//        // Test for list case
//        List<Person> entities = new ArrayList<>();
//        p = new Person();
//        //Not set deletion flag
//        entities.add(p);
//        
//        p = new Person();
//        p.setProvinceID(ACTIVE);
//        entities.add(p);
//        
//        p = new Person();
//        p.setProvinceID(DELETED);
//        entities.add(p);
//        
//        dao.validateInput(entities);
//    }

    @Test
    public void testProcessOutput() throws Exception {
        LogicDeletionDao<Person> dao = buildDao(true, true);
        
        Person p;
        p = new Person();
        p.setProvinceID(ACTIVE);
        assertNotNull(dao.processOutput(p));
        
        p = new Person();
        //Not set deletion flag
        assertNotNull(dao.processOutput(p));
        
        p.setProvinceID(DELETED);
        assertNull(dao.processOutput(p));
        
        // Test for list case
        List<Person> entities = new ArrayList<>();
        p = new Person();
        p.setProvinceID(DELETED);
        entities.add(p);

        p = new Person();
        p.setProvinceID(ACTIVE);
        entities.add(p);
        
        p = new Person();
        //Not set deletion flag
        entities.add(p);
        
        entities = dao.processOutput(entities);
        assertEquals(2, entities.size());
        assertFalse(dao.isDeleted(entities.get(0)));
        assertFalse(dao.isDeleted(entities.get(1)));
    }
//
//    @Test
//    public void testNotProcessOutput() throws Exception {
//        LogicDeletionDao<Person> dao = buildDao(true, false);
//        
//        Person p;
//        p = new Person();
//        p.setProvinceID(ACTIVE);
//        assertNotNull(dao.processOutput(p));
//        
//        p = new Person();
//        //Not set deletion flag
//        assertNotNull(dao.processOutput(p));
//        
//        p.setProvinceID(DELETED);
//        assertNotNull(dao.processOutput(p));
//        
//        // Test for list case
//        List<Person> entities = new ArrayList<>();
//        p = new Person();
//        p.setProvinceID(DELETED);
//        entities.add(p);
//
//        p = new Person();
//        p.setProvinceID(ACTIVE);
//        entities.add(p);
//        
//        p = new Person();
//        //Not set deletion flag
//        entities.add(p);
//        
//        entities = dao.processOutput(entities);
//        assertEquals(3, entities.size());
//        assertTrue(dao.isDeleted(entities.get(0)));
//        assertFalse(dao.isDeleted(entities.get(1)));
//        assertFalse(dao.isDeleted(entities.get(2)));
//    }
//    
    @Test
    public void testSetActiveFlag() throws Exception {
        LogicDeletionDao<Person> dao = buildDao(true, true);
        
        Person p;
        p = new Person();
        p.setProvinceID(ACTIVE);
        dao.setActiveFlag(p);
        assertFalse(dao.isDeleted(p));
        
        p = new Person();
        //Not set deletion flag
        dao.setActiveFlag(p);
        assertFalse(dao.isDeleted(p));
        
        p = new Person();
        p.setProvinceID(DELETED);
        dao.setActiveFlag(p);
        assertFalse(dao.isDeleted(p));
        
        // Test for list case
        List<Person> entities = new ArrayList<>();
        p = new Person();
        p.setProvinceID(DELETED);
        entities.add(p);

        p = new Person();
        p.setProvinceID(ACTIVE);
        entities.add(p);
        
        p = new Person();
        //Not set deletion flag
        entities.add(p);
        
        dao.setActiveFlag(entities);
        
        assertFalse(dao.containsDeleted(entities));
    }
    
    @Test
    public void testSetDeletionFlag() throws Exception {
        LogicDeletionDao<Person> dao = buildDao(true, true);
        
        Person p;
        p = new Person();
        p.setProvinceID(ACTIVE);
        dao.setDeletionFlag(p);
        assertTrue(dao.isDeleted(p));
        
        p = new Person();
        //Not set deletion flag
        dao.setDeletionFlag(p);
        assertTrue(dao.isDeleted(p));
        
        p = new Person();
        p.setProvinceID(DELETED);
        dao.setDeletionFlag(p);
        assertTrue(dao.isDeleted(p));
        
        // Test for list case
        List<Person> entities = new ArrayList<>();
        p = new Person();
        p.setProvinceID(DELETED);
        entities.add(p);

        p = new Person();
        p.setProvinceID(ACTIVE);
        entities.add(p);
        
        p = new Person();
        //Not set deletion flag
        entities.add(p);
        
        dao.setDeletionFlag(entities);
        
        assertTrue(dao.containsDeleted(entities));
    }
    
    @Test
    public void testClearDeletionFlag() throws Exception {
        LogicDeletionDao<Person> dao = buildDao(true, true);
        
        Person p;
        p = new Person();
        p.setProvinceID(ACTIVE);
        dao.clearDeletionFlag(p);
        assertFalse(dao.isDeleted(p));
        
        p = new Person();
        //Not set deletion flag
        dao.clearDeletionFlag(p);
        assertFalse(dao.isDeleted(p));
        
        p = new Person();
        p.setProvinceID(DELETED);
        dao.clearDeletionFlag(p);
        assertFalse(dao.isDeleted(p));
        
        // Test for list case
        List<Person> entities = new ArrayList<>();
        p = new Person();
        p.setProvinceID(DELETED);
        entities.add(p);

        p = new Person();
        p.setProvinceID(ACTIVE);
        entities.add(p);
        
        p = new Person();
        //Not set deletion flag
        entities.add(p);
        
        dao.clearDeletionFlag(entities);
        
        assertFalse(dao.containsDeleted(entities));
    }
}