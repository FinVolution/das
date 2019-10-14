package com.ppdai.das.client.sqlbuilder;

import static com.ppdai.das.client.SegmentConstants.*;
import static com.ppdai.das.client.SqlBuilder.*;
import static com.ppdai.das.client.sqlbuilder.Person.PERSON;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.sql.JDBCType;
import java.util.List;

import com.ppdai.das.client.delegate.local.DasBuilderContext;
import com.ppdai.das.client.Hints;

import com.ppdai.das.core.enums.ParameterDirection;
import org.junit.Test;

import com.ppdai.das.client.Parameter;
import com.ppdai.das.client.ParameterDefinition;

import com.ppdai.das.client.SqlBuilder;
import com.ppdai.das.client.TableDefinition;
import com.ppdai.das.client.sqlbuilder.Person.PersonDefinition;

import junit.framework.Assert;

public class SqlBuilderTest {
    private BuilderContext ctx = new DefaultBuilderContext();

    BuilderContext shardedBC = new BuilderContext() {
        @Override
        public String wrapName(String name) {
            return name;
        }
        
        @Override
        public String locateTableName(Table table) {
            return table.getName() + "_" + table.getShardId();
        }
        
        @Override
        public String locateTableName(TableDefinition definition) {
            return definition.getShardId() == null ? definition.getName() : definition.getName() + "_" + definition.getShardId();
        }

        @Override
        public String declareTableName(String name) {
            return name;
        }

        @Override
        public String getPageTemplate() {
            return Page.EMPTY;
        }
    };

    @Test
    public void testEmpty( ) {
        SqlBuilder builder = new SqlBuilder();
        String sql = builder.build(ctx);
        assertEquals("", sql);
    }
    
    @Test
    public void testNested( ) {
        PersonDefinition p = PERSON.as("p");
        
        SqlBuilder builder = select(PERSON.PeopleID, PERSON.CityID).from(PERSON).where("CityID IN", bracket(select(p.CityID).from(p).where("CityID IN", bracket(select(p.CityID).from(p)))));
        
        assertEquals("SELECT person.PeopleID, person.CityID FROM person WHERE CityID IN (SELECT p.CityID FROM person p WHERE CityID IN (SELECT p.CityID FROM person p))",builder.build(ctx));        
    }
    
    @Test
    public void testAppend( ) {
        SqlBuilder builder = new SqlBuilder();
        builder.append("ABC");
        assertEquals("ABC", builder.build(ctx));
        
        builder = new SqlBuilder();
        builder.append("ABC", "DEF");
        assertEquals("ABC DEF", builder.build(ctx));

        builder = new SqlBuilder();
        builder.append("ABC", select(PERSON.CityID).from(PERSON));
        
        assertEquals("ABC SELECT person.CityID FROM person", builder.build(ctx));
    }

    @Test
    public void testAppendWhen( ) {
        SqlBuilder builder = new SqlBuilder();
        builder.appendTemplateWhen(true, "ABC");
        assertEquals("ABC", builder.build(ctx));


        builder = new SqlBuilder();
        builder.appendWhen(true, "ABC", "DEF");
        assertEquals("ABC DEF", builder.build(ctx));

        builder = new SqlBuilder();
        builder.appendTemplateWhen(false, "ABC");
        assertEquals("", builder.build(ctx));
        
        builder = new SqlBuilder();
        builder.appendWhen(false, "ABC", "DEF");
        assertEquals("", builder.build(ctx));

        builder = new SqlBuilder();
        builder.appendWhen(false, "ABC", "DEF");
    }

    @Test
    public void testAppendWhenParameter( ) {
        SqlBuilder builder = new SqlBuilder();
        builder.appendTemplateWhen(true, "ABC ?", Parameter.integerOf("aaa", 111));
        assertEquals("ABC ?", builder.build(ctx));
        List<Parameter> pl = builder.buildParameters();
        assertEquals(1, pl.size());
        assertX(pl.get(0), "aaa", JDBCType.INTEGER, 111);



        builder = new SqlBuilder();
        builder.appendTemplateWhen(true, "? ABC ?", Parameter.integerOf("aaa", 111), Parameter.varcharOf("bbb", "222"));
        assertEquals("? ABC ?", builder.build(ctx));
        pl = builder.buildParameters();
        assertEquals(2, pl.size());
        assertX(pl.get(0), "aaa", JDBCType.INTEGER, 111);
        assertX(pl.get(1), "bbb", JDBCType.VARCHAR, "222");

        builder = new SqlBuilder();
        builder.appendTemplateWhen(false, "ABC ? ", Parameter.integerOf("aaa", 111));
        assertEquals("", builder.build(ctx));
        
        builder = new SqlBuilder();
        builder.appendTemplateWhen(false, "? ABC ?", Parameter.integerOf("aaa", 111), Parameter.varcharOf("bbb", "222"));
        assertEquals("", builder.build(ctx));
        pl = builder.buildParameters();
        assertEquals(0, pl.size());

        builder = new SqlBuilder();
        builder.appendTemplateWhen(false, "? ABC ?", Parameter.integerOf("aaa", 111), Parameter.varcharOf("bbb", "222"));
        pl = builder.buildParameters();
        assertEquals(0, pl.size());
    }

    @Test
    public void testAppendWhenParameterDefinition( ) {
        SqlBuilder builder = new SqlBuilder();
        builder.appendBatchTemplateWhen(true, "ABC ?", ParameterDefinition.integerVar("aaa"));
        assertEquals("ABC ?", builder.build(ctx));
        List<ParameterDefinition> pl = builder.buildDefinitions();
        assertEquals(1, pl.size());
        assertX(pl.get(0), "aaa", JDBCType.INTEGER, false);



        builder = new SqlBuilder();
        builder.appendBatchTemplateWhen(true, "? ABC ?", ParameterDefinition.integerVar("aaa"), ParameterDefinition.varcharVar("bbb"));
        assertEquals("? ABC ?", builder.build(ctx));
        pl = builder.buildDefinitions();
        assertEquals(2, pl.size());
        assertX(pl.get(0), "aaa", JDBCType.INTEGER, false);
        assertX(pl.get(1), "bbb", JDBCType.VARCHAR, false);

        builder = new SqlBuilder();
        builder.appendBatchTemplateWhen(false, "ABC ? ", ParameterDefinition.integerVar("aaa"));
        assertEquals("", builder.build(ctx));
        
        builder = new SqlBuilder();
        builder.appendBatchTemplateWhen(false, "? ABC ?", ParameterDefinition.integerVar("aaa"), ParameterDefinition.varcharVar("bbb"));
        assertEquals("", builder.build(ctx));
        pl = builder.buildDefinitions();
        assertEquals(0, pl.size());

        builder = new SqlBuilder();
        builder.appendBatchTemplateWhen(false, "? ABC ?", ParameterDefinition.integerVar("aaa"), ParameterDefinition.varcharVar("bbb"));
        pl = builder.buildDefinitions();
        assertEquals(0, pl.size());
    }

    @Test
    public void testAppendWith( ) {
        SqlBuilder builder = new SqlBuilder();
        builder.appendWith(text("|"), "ABC", "DEF", "XYZ");
        assertEquals("ABC | DEF | XYZ", builder.build(ctx));
    }
    
    @Test
    public void testAppendWithWhen( ) {
        SqlBuilder builder = new SqlBuilder();
        builder.appendWithWhen(true, text("|"), "ABC", "DEF", "XYZ");
        assertEquals("ABC | DEF | XYZ", builder.build(ctx));
        
        builder = new SqlBuilder();
        builder.appendWithWhen(false, text("|"), "ABC", "DEF", "XYZ");
        assertEquals("", builder.build(ctx));
    }
    
    @Test
    public void testAppendPlaceHolder( ) {
        SqlBuilder builder = new SqlBuilder();
        builder.appendPlaceHolder(5);
        assertEquals("?, ?, ?, ?, ?", builder.build(ctx));
    }
    
    @Test
    public void testAppendPlaceHolderWhen( ) {
        SqlBuilder builder = new SqlBuilder();
        builder.appendPlaceHolderWhen(true, 5);
        assertEquals("?, ?, ?, ?, ?", builder.build(ctx));
        
        builder = new SqlBuilder();
        builder.appendPlaceHolderWhen(false, 5);
        assertEquals("", builder.build(ctx));
    }
    
    @Test
    public void testSelectAll( ) {
        assertEquals("SELECT *", selectAll().build(ctx));
        assertEquals("SELECT * FROM table", selectAll().from("table").build(ctx));
    }

    @Test
    public void testSelectCount( ) {
        assertEquals("SELECT count(1)", selectCount().build(ctx));
        assertEquals("SELECT count(1) FROM table", selectCount().from("table").build(ctx));
    }

    @Test
    public void testSelect( ) {
        assertEquals("SELECT", select().build(ctx));
        
        assertEquals("SELECT A", select("A").build(ctx));
        
        assertEquals("SELECT A, B, C", select("A","B","C").build(ctx));
        
        assertEquals("SELECT person.PeopleID", select(PERSON.PeopleID).build(ctx));

        assertEquals("SELECT person.PeopleID, person.Name", select(PERSON.PeopleID, PERSON.Name).build(ctx));

        assertEquals("SELECT person.PeopleID AS id", select(PERSON.PeopleID.as("id")).build(ctx));
        
        assertEquals("SELECT person.PeopleID AS id, person.Name AS name", select(PERSON.PeopleID.as("id"), PERSON.Name.as("name")).build(ctx));

        assertEquals("SELECT person.PeopleID, person.Name, person.CityID, person.ProvinceID, person.CountryID, person.DataChange_LastTime", select(PERSON.allColumns()).build(ctx));
        
        PersonDefinition p = PERSON.as("p");
        
        assertEquals("SELECT p.PeopleID", select(p.PeopleID).build(ctx));
        
        assertEquals("SELECT p.PeopleID, p.Name, p.CityID", select(p.PeopleID, p.Name, p.CityID).build(ctx));
        
        assertEquals("SELECT p.PeopleID, p.Name, p.CityID, p.ProvinceID, p.CountryID, p.DataChange_LastTime", select(p.allColumns()).build(ctx));

        assertEquals("SELECT p.PeopleID, p.Name, p.CityID, p.ProvinceID, p.CountryID, p.DataChange_LastTime", select(PERSON.as("p").allColumns()).build(new CtripBuilderContextTest("person")));
        
        assertEquals("SELECT p.PeopleID AS id", select(p.PeopleID.as("id")).build(ctx));
        
        assertEquals("SELECT p.PeopleID", select(p.PeopleID).build(ctx));
        
        assertEquals("SELECT p.PeopleID AS id, p.Name AS name", select(p.PeopleID.as("id"), p.Name.as("name")).build(ctx));

        Table t = new Table("test");
        Column nameNV = t.nvarcharColumn("nameNV");
        Column nameV = t.varcharColumn("nameV");
        
        assertEquals("SELECT test.nameNV", select(nameNV).build(ctx));
        
        assertEquals("SELECT test.nameNV, test.nameV", select(nameNV, nameV).build(ctx));
        
        assertEquals("SELECT test.nameNV, test.nameV", select(t.allColumns()).build(ctx));
        
        assertEquals("SELECT test.nameNV AS NV", select(nameNV.as("NV")).build(ctx));
        
        //assertEquals("SELECT test.nameNV AS NV, test.nameV AS V", select(nameNV, nameV.as("V")).build(ctx));
        try {
            // Can not be created twice for same name
            t.nvarcharColumn("nameNV");
            fail();
        }catch (Throwable e) {
        }

        try {
            //Can not be renamed twice
            nameNV.as("xxx");
            fail();
        }catch (Throwable e) {
        }

    }

    @Test
    public void testSelectDistinct( ) {
        assertEquals("SELECT DISTINCT", selectDistinct().build(ctx));
        
        assertEquals("SELECT DISTINCT A", selectDistinct("A").build(ctx));
        
        assertEquals("SELECT DISTINCT A, B, C", selectDistinct("A","B","C").build(ctx));
        
        assertEquals("SELECT DISTINCT person.PeopleID", selectDistinct(PERSON.PeopleID).build(ctx));
        
        assertEquals("SELECT DISTINCT person.PeopleID, person.Name", selectDistinct(PERSON.PeopleID, PERSON.Name).build(ctx));

        assertEquals("SELECT DISTINCT person.PeopleID AS id", selectDistinct(PERSON.PeopleID.as("id")).build(ctx));
        
        assertEquals("SELECT DISTINCT person.PeopleID AS id, person.Name AS name", selectDistinct(PERSON.PeopleID.as("id"), PERSON.Name.as("name")).build(ctx));
        
        assertEquals("SELECT DISTINCT person.PeopleID, person.Name, person.CityID, person.ProvinceID, person.CountryID, person.DataChange_LastTime", selectDistinct(PERSON.allColumns()).build(ctx));
        
        PersonDefinition p = PERSON.as("p");
        
        assertEquals("SELECT DISTINCT p.PeopleID", selectDistinct(p.PeopleID).build(ctx));
        
        assertEquals("SELECT DISTINCT p.PeopleID, p.Name, p.CityID", selectDistinct(p.PeopleID, p.Name, p.CityID).build(ctx));
        
        assertEquals("SELECT DISTINCT p.PeopleID, p.Name, p.CityID, p.ProvinceID, p.CountryID, p.DataChange_LastTime", selectDistinct(p.allColumns()).build(ctx));
        
        assertEquals("SELECT DISTINCT p.PeopleID, p.Name, p.CityID, p.ProvinceID, p.CountryID, p.DataChange_LastTime", selectDistinct(PERSON.as("p").allColumns()).build(ctx));
        
        assertEquals("SELECT DISTINCT p.PeopleID AS id", selectDistinct(p.PeopleID.as("id")).build(ctx));
        
        assertEquals("SELECT DISTINCT p.PeopleID", selectDistinct(p.PeopleID).build(ctx));
        
        assertEquals("SELECT DISTINCT p.PeopleID AS id, p.Name AS name", selectDistinct(p.PeopleID.as("id"), p.Name.as("name")).build(ctx));
        
        Table t = new Table("test");
        Column nameNV = t.nvarcharColumn("nameNV");
        Column nameV = t.varcharColumn("nameV");
        
        assertEquals("SELECT DISTINCT test.nameNV", selectDistinct(nameNV).build(ctx));
        
        assertEquals("SELECT DISTINCT test.nameNV, test.nameV", selectDistinct(nameNV, nameV).build(ctx));
        
        assertEquals("SELECT DISTINCT test.nameNV, test.nameV", selectDistinct(t.allColumns()).build(ctx));

        assertEquals("SELECT DISTINCT test.nameNV AS NV", selectDistinct(nameNV.as("NV")).build(ctx));
        
        assertEquals("SELECT DISTINCT test.nameNV AS NV, test.nameV AS V", selectDistinct(nameNV, nameV.as("V")).build(ctx));
    }
    
    @Test
    public void testSelectTop( ) {
        assertEquals("SELECT TOP 1", selectTop(1).build(ctx));
        
        assertEquals("SELECT TOP 1 A", selectTop(1, "A").build(ctx));
        
        assertEquals("SELECT TOP 1 A, B, C", selectTop(1, "A","B","C").build(ctx));
        
        assertEquals("SELECT TOP 1 person.PeopleID", selectTop(1, PERSON.PeopleID).build(ctx));
        
        assertEquals("SELECT TOP 1 person.PeopleID, person.Name", selectTop(1, PERSON.PeopleID, PERSON.Name).build(ctx));

        assertEquals("SELECT TOP 1 person.PeopleID AS id", selectTop(1, PERSON.PeopleID.as("id")).build(ctx));
        
        assertEquals("SELECT TOP 1 person.PeopleID AS id, person.Name AS name", selectTop(1, PERSON.PeopleID.as("id"), PERSON.Name.as("name")).build(ctx));
        
        assertEquals("SELECT TOP 1 person.PeopleID, person.Name, person.CityID, person.ProvinceID, person.CountryID, person.DataChange_LastTime", selectTop(1, PERSON.allColumns()).build(ctx));
        
        PersonDefinition p = PERSON.as("p");
        
        assertEquals("SELECT TOP 1 p.PeopleID", selectTop(1, p.PeopleID).build(ctx));
        
        assertEquals("SELECT TOP 1 p.PeopleID, p.Name, p.CityID", selectTop(1, p.PeopleID, p.Name, p.CityID).build(ctx));
        
        assertEquals("SELECT TOP 1 p.PeopleID, p.Name, p.CityID, p.ProvinceID, p.CountryID, p.DataChange_LastTime", selectTop(1, p.allColumns()).build(ctx));
        
        assertEquals("SELECT TOP 1 p.PeopleID, p.Name, p.CityID, p.ProvinceID, p.CountryID, p.DataChange_LastTime", selectTop(1, PERSON.as("p").allColumns()).build(ctx));
        
        assertEquals("SELECT TOP 1 p.PeopleID AS id", selectTop(1, p.PeopleID.as("id")).build(ctx));
        
        assertEquals("SELECT TOP 1 p.PeopleID", selectTop(1, p.PeopleID).build(ctx));
        
        assertEquals("SELECT TOP 1 p.PeopleID AS id, p.Name AS name", selectTop(1, p.PeopleID.as("id"), p.Name.as("name")).build(ctx));
        
        Table t = new Table("test");
        Column nameNV = t.nvarcharColumn("nameNV");
        Column nameV = t.varcharColumn("nameV");
        
        assertEquals("SELECT TOP 1 test.nameNV", selectTop(1, nameNV).build(ctx));
        
        assertEquals("SELECT TOP 1 test.nameNV, test.nameV", selectTop(1, nameNV, nameV).build(ctx));
        
        assertEquals("SELECT TOP 1 test.nameNV, test.nameV", selectTop(1, t.allColumns()).build(ctx));

        assertEquals("SELECT TOP 1 test.nameNV AS NV", selectTop(1, nameNV.as("NV")).build(ctx));
        
        assertEquals("SELECT TOP 1 test.nameNV AS NV, test.nameV AS V", selectTop(1, nameNV, nameV.as("V")).build(ctx));
    }
    
    @Test
    public void testFrom( ) {
        assertEquals("FROM", new SqlBuilder().from().build(ctx));
        
        assertEquals("FROM ABC", new SqlBuilder().from("ABC").build(ctx));
        
        assertEquals("FROM person", new SqlBuilder().from(PERSON).build(ctx));

        assertEquals("FROM person p", new SqlBuilder().from(PERSON.as("p")).build(ctx));
        
        Table t = new Table("test");

        assertEquals("FROM test", new SqlBuilder().from(t).build(ctx));

        assertEquals("FROM test T", new SqlBuilder().from(t.as("T")).build(ctx));
        try {
            //Can not be renamed twice
            t.as("xxx");
            fail();
        }catch (Throwable e) {
        }
    }

    static public void assertEquals2(Object expected, Object actual1, Object actual2) {
        assertEquals(null, expected, actual1);
        assertEquals(null, expected, actual2);
    }

    static class CtripBuilderContextTest extends DasBuilderContext{

        public CtripBuilderContextTest(String logicDbName) {
            super("das-test", logicDbName);
        }

        public CtripBuilderContextTest(String logicDbName, Hints ctripHints, List<Parameter> parameters) {
            super("das-test", logicDbName, ctripHints, parameters);
        }

        public CtripBuilderContextTest(String logicDbName, Hints ctripHints, List<Parameter> parameters, SqlBuilder builder) {
            super("das-test", logicDbName, ctripHints, parameters, builder);
        }

//        @Override
//        public String locateTableName(TableDefinition definition) {
//            return definition.getName();
//        }
//
//        @Override
//        public String locateTableName(Table table) {
//            return table.getName();
//        }
//
        @Override
        public String wrapName(String name) {
            return name;
        }

        @Override
        public String declareTableName(String name) {
            return name;
        }

        @Override
        public String locate(String rawTableName, String tableShardId, Object tableShardValue) {
            if(tableShardId == null){
                return rawTableName;
            } else {
                return rawTableName + "_" + tableShardId;
            }
           // return rawTableName;// + tableShardId +tableShardValue;
        }
    }

    @Test
    public void testSelectAllFrom( ) {
        assertEquals("SELECT person.PeopleID, person.Name, person.CityID, person.ProvinceID, person.CountryID, person.DataChange_LastTime FROM person", new SqlBuilder().selectAllFrom(PERSON).build(ctx));

        PersonDefinition p = PERSON.as("p");
        new SqlBuilder().selectAllFrom(p).build(ctx);
      // Table t = new Table("test");
      // Column nameNV = t.nvarcharColumn("nameNV");
      // Column nameV = t.varcharColumn("nameV");

      // assertEquals("SELECT test.nameNV, test.nameV", new SqlBuilder().selectAllFrom(t).build(ctx));
    }

    @Test
    public void testSelectShard( ) {
        PersonDefinition p = PERSON.inShard("0");

        assertEquals("SELECT person_0.PeopleID, person_0.Name FROM person_0", select(p.PeopleID, p.Name).from(p).build(shardedBC));
        
        assertEquals("SELECT person_0.PeopleID AS id FROM person_0", select(p.PeopleID.as("id")).from(p).build(shardedBC));
        
        assertEquals("SELECT person_0.PeopleID AS id, person_0.Name AS name FROM person_0", select(p.PeopleID.as("id"), p.Name.as("name")).from(p).build(shardedBC));
        
        assertEquals("SELECT person_0.PeopleID, person_0.Name, person_0.CityID, person_0.ProvinceID, person_0.CountryID, person_0.DataChange_LastTime FROM person_0", select(p.allColumns()).from(p).build(shardedBC));
        
        assertEquals("SELECT person.PeopleID, person.Name, person.CityID, person.ProvinceID, person.CountryID, person.DataChange_LastTime FROM person", select(PERSON.allColumns()).from(PERSON).build(shardedBC));
        
        p = p.as("p");
        
        assertEquals("SELECT p.PeopleID FROM person_0 p", select(p.PeopleID).from(p).build(shardedBC));
        
        assertEquals("SELECT p.PeopleID, p.Name, p.CityID FROM person_0 p", select(p.PeopleID, p.Name, p.CityID).from(p).build(shardedBC));
        
        assertEquals("SELECT p.PeopleID, p.Name, p.CityID, p.ProvinceID, p.CountryID, p.DataChange_LastTime FROM person_0 p", select(p.allColumns()).from(p).build(shardedBC));
        
        assertEquals("SELECT p.PeopleID, p.Name, p.CityID, p.ProvinceID, p.CountryID, p.DataChange_LastTime FROM person_0 p", select(PERSON.as("p").allColumns()).from(p).build(shardedBC));

        assertEquals("SELECT p.PeopleID AS id, p.Name AS name FROM person_0 p", select(p.PeopleID.as("id"), p.Name.as("name")).from(p).build(shardedBC));
        
        Table t = new Table("test").inShard("0");
        Column nameNV = t.nvarcharColumn("nameNV");
        Column nameV = t.varcharColumn("nameV");
        
        assertEquals("SELECT test_0.nameNV", select(nameNV).build(shardedBC));
        
        assertEquals("SELECT test_0.nameNV, test_0.nameV", select(nameNV, nameV).build(shardedBC));
        
        assertEquals("SELECT test_0.nameNV, test_0.nameV", select(t.allColumns()).build(shardedBC));
        
        assertEquals("SELECT test_0.nameNV AS NV", select(nameNV.as("NV")).build(shardedBC));
        
        assertEquals("SELECT test_0.nameNV AS NV, test_0.nameV AS V", select(nameNV, nameV.as("V")).build(shardedBC));
    }
    
    @Test
    public void testFromShard( ) {

        assertEquals("FROM", new SqlBuilder().from().build(ctx));
        
        assertEquals("FROM person_0", new SqlBuilder().from(PERSON.inShard("0")).build(shardedBC));

        Table t = new Table("test");

        assertEquals("FROM test_0", new SqlBuilder().from(t.inShard("0")).build(shardedBC));
        try {
            //Can not be renamed twice
            t.as("xxx");
            fail();
        }catch (Throwable e) {
        }
    }
    
    @Test
    public void testSelectFrom( ) {
        assertEquals("SELECT person.PeopleID, person.Name, person.CityID, person.ProvinceID, person.CountryID, person.DataChange_LastTime FROM person", 
                select(PERSON.allColumns()).from(PERSON).build(ctx));
        PersonDefinition p = PERSON.as("p");
        assertEquals("SELECT p.PeopleID, p.Name, p.CityID, p.ProvinceID, p.CountryID, p.DataChange_LastTime FROM person p", select(p.allColumns()).from(p).build(ctx));
        
        Table t = new Table("test");
        Column nameNV = t.nvarcharColumn("nameNV");
        Column nameV = t.varcharColumn("nameV");
        assertEquals("SELECT test.nameNV, test.nameV FROM test", select(t.allColumns()).from(t).build(ctx));
        
        t.as("T");
        assertEquals("SELECT T.nameNV, T.nameV FROM test T", select(t.allColumns()).from(t).build(ctx));
        
        assertEquals("SELECT T.nameNV, T.nameV FROM test T", select(nameNV, nameV).from(t).build(ctx));
    }
    
    @Test
    public void testInsert( ) {
        PersonDefinition p = PERSON;
        
        assertEquals("INSERT INTO person (CityID, CountryID)", new SqlBuilder().insertInto(p, p.CityID, p.CountryID).build(ctx));
        
        assertEquals("INSERT INTO person (CityID, CountryID) VALUES (?, ?)", new SqlBuilder().insertInto(p, p.CityID, p.CountryID).values(p.CityID.of(100), p.CountryID.of(200)).build(ctx));
    }
    
    @Test
    public void testUpdate( ) {
        PersonDefinition p = PERSON;
        
        assertEquals("UPDATE person", new SqlBuilder().update(Person.PERSON).build(ctx));
        
        assertEquals("SET person.Name = ?, person.CountryID = ?", new SqlBuilder().set(p.Name.eq("Tom"), p.CountryID.eq(100)  ).build(ctx));
    }

    @Test
    public void testUpdateNullable( ) {
        PersonDefinition p = PERSON;
        assertEquals("UPDATE person SET person.CityID = ?, person.CountryID = ? WHERE 1=1 AND person.CityID = ? AND person.CountryID = ?",
                SqlBuilder.update(p).set(p.CityID.eq(1), p.Name.eq(null).nullable(), p.CountryID.eq(2).nullable()).where().includeAll()
                        .and(p.CityID.eq(1))
                        .and(p.Name.eq(null).nullable())
                        .and(p.CountryID.eq(2).nullable())
                        .build(ctx));
    }

    @Test
    public void testJoin( ) {
        PersonDefinition p = PERSON.as("p");

        assertEquals("SELECT p.PeopleID, p.Name, p.CityID FROM person p JOIN", select(p.PeopleID, p.Name, p.CityID).from(p).join().build(ctx));
        
        assertEquals("SELECT p.PeopleID, p.Name, p.CityID FROM person p JOIN person", select(p.PeopleID, p.Name, p.CityID).from(p).join(PERSON).build(ctx));
        
        Table t = new Table("test");
        Column nameNV = t.nvarcharColumn("nameNV");
        Column nameV = t.varcharColumn("nameV");
        assertEquals("SELECT test.nameNV, test.nameV FROM test JOIN", select(t.allColumns()).from(t).join().build(ctx));
        
        t.as("T");
        assertEquals("SELECT T.nameNV, T.nameV FROM test T JOIN person", select(t.allColumns()).from(t).join(PERSON).build(ctx));
    }

    @Test
    public void testXJoin( ) {
        PersonDefinition p = PERSON.as("p");

        assertEquals("SELECT p.PeopleID, p.Name, p.CityID FROM person p LEFT JOIN person t", select(p.PeopleID, p.Name, p.CityID).from(p).leftJoin(PERSON.as("t")).build(ctx));
        assertEquals("SELECT p.PeopleID, p.Name, p.CityID FROM person p RIGHT JOIN person t", select(p.PeopleID, p.Name, p.CityID).from(p).rightJoin(PERSON.as("t")).build(ctx));
        assertEquals("SELECT p.PeopleID, p.Name, p.CityID FROM person p INNER JOIN person t", select(p.PeopleID, p.Name, p.CityID).from(p).innerJoin(PERSON.as("t")).build(ctx));
        assertEquals("SELECT p.PeopleID, p.Name, p.CityID FROM person p FULL JOIN person t", select(p.PeopleID, p.Name, p.CityID).from(p).fullJoin(PERSON.as("t")).build(ctx));
        
        Table t = new Table("test");
        Column nameNV = t.nvarcharColumn("nameNV");
        Column nameV = t.varcharColumn("nameV");
        assertEquals("SELECT test.nameNV, test.nameV FROM test LEFT JOIN", select(t.allColumns()).from(t).leftJoin().build(ctx));
        
        t.as("T");
        assertEquals("SELECT T.nameNV, T.nameV FROM test T LEFT JOIN person p", select(nameNV, nameV).from(t).leftJoin(PERSON.as("p")).build(ctx));
        assertEquals("SELECT T.nameNV, T.nameV FROM test T RIGHT JOIN person p", select(nameNV, nameV).from(t).rightJoin(PERSON.as("p")).build(ctx));
        assertEquals("SELECT T.nameNV, T.nameV FROM test T INNER JOIN person p", select(nameNV, nameV).from(t).innerJoin(PERSON.as("p")).build(ctx));
        assertEquals("SELECT T.nameNV, T.nameV FROM test T FULL JOIN person p", select(nameNV, nameV).from(t).fullJoin(PERSON.as("p")).build(ctx));
        assertEquals("SELECT T.nameNV, T.nameV FROM test T CROSS JOIN person p", select(nameNV, nameV).from(t).crossJoin(PERSON.as("p")).build(ctx));
    }

    @Test
    public void testOn( ) {
        PersonDefinition p = PERSON.as("p");
        Table t = new Table("test").as("T");
        Column nameNV = t.nvarcharColumn("nameNV");
        Column nameV = t.varcharColumn("nameV");

        assertEquals("SELECT p.PeopleID, p.Name, p.CityID, T.nameNV, T.nameV FROM person p JOIN test T ON", select(p.PeopleID, p.Name, p.CityID, nameNV, nameV).from(p).join(t).on().build(ctx));
        assertEquals("SELECT p.PeopleID, p.Name, p.CityID, T.nameNV, T.nameV FROM person p LEFT JOIN test T ON p.PeopleID = T.nameNV", select(p.PeopleID, p.Name, p.CityID, nameNV, nameV).from(p).leftJoin(t).on(p.PeopleID.equal(nameNV)).build(ctx));
        assertEquals("SELECT p.PeopleID, p.Name, p.CityID, T.nameNV, T.nameV FROM person p LEFT JOIN test T ON p.PeopleID = T.nameNV AND p.CountryID > T.nameV", select(p.PeopleID, p.Name, p.CityID, nameNV, nameV).from(p).leftJoin(t).on(p.PeopleID.equal(nameNV), AND, p.CountryID.greaterThan(nameV)).build(ctx));

        nameNV.as("NV");
        nameV.as("V");
        
        assertEquals("SELECT p.PeopleID, p.Name, p.CityID, T.nameNV AS NV, T.nameV AS V FROM person p JOIN test T ON", select(p.PeopleID, p.Name, p.CityID, nameNV, nameV).from(p).join(t).on().build(ctx));
        assertEquals("SELECT p.PeopleID, p.Name, p.CityID, T.nameNV AS NV, T.nameV AS V FROM person p LEFT JOIN test T ON p.PeopleID = T.nameNV", select(p.PeopleID, p.Name, p.CityID, nameNV, nameV).from(p).leftJoin(t).on(p.PeopleID.equal(nameNV)).build(ctx));
        assertEquals("SELECT p.PeopleID, p.Name, p.CityID, T.nameNV AS NV, T.nameV AS V FROM person p LEFT JOIN test T ON p.PeopleID = T.nameNV AND p.CountryID > T.nameV", select(p.PeopleID, p.Name, p.CityID, nameNV, nameV).from(p).leftJoin(t).on(p.PeopleID.equal(nameNV), AND, p.CountryID.greaterThan(nameV)).build(ctx));
        assertEquals("SELECT p.PeopleID, p.Name, p.CityID, T.nameNV AS NV, T.nameV AS V FROM person p LEFT JOIN test T ON p.PeopleID = T.nameNV OR p.CountryID > T.nameV", select(p.PeopleID, p.Name, p.CityID, nameNV, nameV).from(p).leftJoin(t).on(p.PeopleID.equal(nameNV), OR, p.CountryID.greaterThan(nameV)).build(ctx));
    }

    @Test
    public void testUsing( ) {
        PersonDefinition p = PERSON.as("p");
        Table t = new Table("test").as("T");
        Column nameNV = t.nvarcharColumn("nameNV");
        Column nameV = t.varcharColumn("nameV");

        nameNV.as("NV");
        nameV.as("V");
        
        assertEquals("SELECT p.PeopleID, p.Name, p.CityID, T.nameNV AS NV, T.nameV AS V FROM person p JOIN test T USING (nameNV)", select(p.PeopleID, p.Name, p.CityID, nameNV, nameV).from(p).join(t).using(nameNV).build(ctx));
    }

    @Test
    public void testGroupBy( ) {
        PersonDefinition p = PERSON.as("p");
        assertEquals("GROUP BY", new SqlBuilder().groupBy().build(ctx));
        assertEquals("GROUP BY p.CountryID, p.DataChange_LastTime", new SqlBuilder().groupBy(p.CountryID, p.DataChange_LastTime).build(ctx));
        assertEquals("GROUP BY p.PeopleID, p.Name, p.CityID, p.ProvinceID, p.CountryID, p.DataChange_LastTime", new SqlBuilder().groupBy((Object[])p.allColumns()).build(ctx));
        
        Table t = new Table("test").as("T");
        Column nameNV = t.nvarcharColumn("nameNV").as("NV");
        Column nameV = t.varcharColumn("nameV").as("V");

        assertEquals("GROUP BY p.CountryID, T.nameNV", new SqlBuilder().groupBy(p.CountryID, nameNV).build(ctx));
        assertEquals("GROUP BY T.nameNV, T.nameV", new SqlBuilder().groupBy((Object[])t.allColumns()).build(ctx));
    }

    @Test
    public void testHaving( ) {
        PersonDefinition p = PERSON.as("p");

        Table t = new Table("test").as("T");
        Column nameNV = t.nvarcharColumn("nameNV").as("NV");
        Column nameV = t.varcharColumn("nameV").as("V");

        assertEquals("HAVING p.CityID = T.nameNV AND p.PeopleID > T.nameV", new SqlBuilder().having(p.CityID.equal(nameNV), AND, p.PeopleID.greaterThan(nameV)).build(ctx));
        assertEquals("HAVING p.CityID = person.CityID AND p.PeopleID > person.CountryID", new SqlBuilder().having(p.CityID.equal(PERSON.CityID), AND, p.PeopleID.greaterThan(PERSON.CountryID)).build(ctx));
    }

    @Test
    public void testOrderBy( ) {
        PersonDefinition p = PERSON.as("p");
        assertEquals("ORDER BY", new SqlBuilder().orderBy().build(ctx));
        assertEquals("ORDER BY p.CountryID ASC, p.DataChange_LastTime ASC", new SqlBuilder().orderBy(p.CountryID, p.DataChange_LastTime).build(ctx));
        assertEquals("ORDER BY p.PeopleID ASC, p.Name ASC, p.CityID ASC, p.ProvinceID ASC, p.CountryID ASC, p.DataChange_LastTime ASC", new SqlBuilder().orderBy((Object[])p.allColumns()).build(ctx));
        
        Table t = new Table("test").as("T");
        Column nameNV = t.nvarcharColumn("nameNV").as("NV");
        Column nameV = t.varcharColumn("nameV").as("V");

        assertEquals("ORDER BY p.CountryID ASC, T.nameNV ASC", new SqlBuilder().orderBy(p.CountryID, nameNV).build(ctx));
        assertEquals("ORDER BY T.nameNV ASC, T.nameV ASC", new SqlBuilder().orderBy((Object[])t.allColumns()).build(ctx));
        
        assertEquals("ORDER BY p.CountryID DESC, T.nameNV DESC", new SqlBuilder().orderBy(p.CountryID.desc(), nameNV.desc()).build(ctx));
    }
    
    @Test
    public void testBrackets( ) {
        assertEquals("(", new SqlBuilder().leftBracket().build(ctx));
        assertEquals(")", new SqlBuilder().rightBracket().build(ctx));
        
        assertEquals("", new SqlBuilder().leftBracket().rightBracket().build(ctx));
        
        assertEquals("", new SqlBuilder().bracket().build(ctx));
        // Auto removed
        assertEquals("", new SqlBuilder().bracket().build(ctx));
        assertEquals("", new SqlBuilder().bracket(bracket(bracket(bracket(bracket())))).build(ctx));
        assertEquals("", new SqlBuilder().bracket(bracket(), bracket(), bracket(), bracket()).build(ctx));
        
        assertEquals("(AAA)", new SqlBuilder().bracket("AAA").build(ctx));
        assertEquals("(AAA BBB)", new SqlBuilder().bracket("AAA", "BBB").build(ctx));
        assertEquals("(AAA person.CityID)", new SqlBuilder().bracket("AAA", PERSON.CityID).build(ctx));
    }

    @Test
    public void testBracketsFly( ) {
        // Auto removed
        assertEquals("", new SqlBuilder().append(bracket(bracket(), bracket())).build(ctx));
        
        
        assertEquals("", new SqlBuilder().append(bracket(bracket(bracket(), bracket()), bracket())).build(ctx));
        
        assertEquals("(AAA)", new SqlBuilder().append(bracket("AAA")).build(ctx));
        assertEquals("(AAA BBB)", new SqlBuilder().append(bracket("AAA", "BBB")).build(ctx));
        assertEquals("(AAA person.CityID)", new SqlBuilder().append(bracket("AAA", PERSON.CityID)).build(ctx));
    }

    @Test
    public void testAnd( ) {
        // Auto removed
        assertEquals("", new SqlBuilder().and().build(ctx));
        assertEquals("aaa AND bbb", new SqlBuilder().append(expression("aaa")).and().append(expression("bbb")).build(ctx));
        assertEquals("aaa", new SqlBuilder().and(expression("aaa")).build(ctx));
    }

    @Test
    public void testAllOf( ) {
        // Auto removed
        assertEquals("", new SqlBuilder().allOf().build(ctx));
        assertEquals("(aaa)", new SqlBuilder().allOf(expression("aaa")).build(ctx));
        assertEquals("(aaa AND bbb)", new SqlBuilder().allOf(expression("aaa"), expression("bbb")).build(ctx));
    }

    @Test
    public void testAllOfFly( ) {
        // Auto removed
        assertEquals("", new SqlBuilder().append(allOf()).build(ctx));
        assertEquals("(aaa)", new SqlBuilder().append(allOf(expression("aaa"))).build(ctx));
        assertEquals("(aaa AND bbb AND ccc)", new SqlBuilder().append(allOf(expression("aaa"), expression("bbb"), expression("ccc"))).build(ctx));
        assertEquals("(aaa AND bbb AND ccc) (aaa AND bbb AND ccc)", new SqlBuilder().append(allOf(expression("aaa"), expression("bbb"), expression("ccc")), allOf(expression("aaa"), expression("bbb"), expression("ccc"))).build(ctx));
    }

    @Test
    public void testOr( ) {
        // Auto removed
        assertEquals("", new SqlBuilder().or().build(ctx));
        assertEquals("aaa OR bbb", new SqlBuilder().append(expression("aaa")).or().append(expression("bbb")).build(ctx));
        assertEquals("aaa", new SqlBuilder().or(expression("aaa")).build(ctx));
        assertEquals("aaa OR bbb", new SqlBuilder().append(expression("aaa")).or(expression("bbb")).build(ctx));
    }

    @Test
    public void testAnyOf( ) {
        // Auto removed
        assertEquals("", new SqlBuilder().anyOf().build(ctx));
        assertEquals("(aaa)", new SqlBuilder().anyOf(expression("aaa")).build(ctx));
        assertEquals("(aaa OR bbb)", new SqlBuilder().anyOf(expression("aaa"), expression("bbb")).build(ctx));
        assertEquals("(aaa OR bbb OR ccc)", new SqlBuilder().anyOf(expression("aaa"), expression("bbb"), expression("ccc")).build(ctx));
    }

    @Test
    public void testAnyOfFly( ) {
        // Auto removed
        assertEquals("", new SqlBuilder().append(anyOf()).build(ctx));
        assertEquals("(aaa OR bbb)", new SqlBuilder().append(anyOf(expression("aaa"), expression("bbb"))).build(ctx));
        assertEquals("(aaa OR bbb OR ccc)", new SqlBuilder().append(anyOf(expression("aaa"), expression("bbb"), expression("ccc"))).build(ctx));
        assertEquals("(aaa OR bbb OR ccc) (aaa OR bbb OR ccc)", new SqlBuilder().append(anyOf(expression("aaa"), expression("bbb"), expression("ccc")), anyOf(expression("aaa"), expression("bbb"), expression("ccc"))).build(ctx));
    }
    
    @Test
    public void testIncludeAll( ) {
        assertEquals("1=1 AND", new SqlBuilder().includeAll().build(ctx));
        
        // Auto removed
        assertEquals("1=1", new SqlBuilder().includeAll().append(expression("aaa").when(false)).build(ctx));
        
    }
    
    @Test
    public void testExcludeAll( ) {
        assertEquals("1<>1 OR", new SqlBuilder().excludeAll().build(ctx));
                
        // Auto removed
        assertEquals("1<>1", new SqlBuilder().excludeAll().append(expression("aaa").when(false)).build(ctx));        
    }

    @Test
    public void testX( ) {
        assertEquals("NOT", new SqlBuilder().not().build(ctx));
        assertEquals("NOT aaa", new SqlBuilder().not(expression("aaa")).build(ctx));
        
        assertEquals("aaa AND bbb", new SqlBuilder().append(AND, expression("aaa"), AND, expression("bbb"), AND, expression("bbb").when(false)).build(ctx));
        assertEquals("", new SqlBuilder().not().append(expression("bbb").when(false)).build(ctx));
        assertEquals("NOT NOT aaa", new SqlBuilder().not().not(expression("aaa")).build(ctx));
        assertEquals("NOT NOT", new SqlBuilder().not().not().build(ctx));
        //TODO shoul it be like below?
        //assertEquals("aaa", new SqlBuilder().not().not(expression("aaa")).build(ctx));
    }

    private void assertX(Parameter param, String name, JDBCType type, Object value) {
        assertX(param, name, type, false); 
        assertEquals(param.getValue(), value);
    }

    private void assertX(ParameterDefinition param, String name, JDBCType type, boolean inValues) {
        assertEquals(ParameterDirection.Input, param.getDirection());
        assertEquals(name, param.getName());
        assertEquals(type, param.getType());
        assertEquals(inValues, param.isInValues());
    }

    @Test
    public void testLimitCount( ) {
        SqlBuilder sb = new SqlBuilder();
        sb.limit(10);
        List<Parameter> pl = sb.buildParameters();
        assertEquals(1, pl.size());
        Parameter p = pl.get(0);
        assertX(pl.get(0), "", JDBCType.INTEGER, 10);
    }

    @Test
    public void testLimitCountDef( ) {
        SqlBuilder sb = new SqlBuilder();
        sb.limit(ParameterDefinition.integerVar("start"));
        assertEquals("LIMIT ?", sb.build(ctx));
        List<ParameterDefinition> pl = sb.buildDefinitions();
        assertEquals(1, pl.size());
        assertX(pl.get(0), "start", JDBCType.INTEGER, false);
    }

    @Test
    public void testLimitStartCount( ) {
        SqlBuilder sb = new SqlBuilder();
        sb.limit(10, 20);
        assertEquals("LIMIT ?, ?", sb.build(ctx));
        List<Parameter> pl = sb.buildParameters();
        assertEquals(2, pl.size());
        Parameter p = pl.get(0);
        assertX(pl.get(0), "", JDBCType.INTEGER, 10);
        assertX(pl.get(1), "", JDBCType.INTEGER, 20);
    }

    @Test
    public void testLimitExceed( ) {
        try{
            SqlBuilder.setLimitThreshold(2);
            new SqlBuilder().limit(2);
            Assert.fail();
        } catch (IllegalArgumentException e){
            Assert.assertNotNull(e);
        } finally {
            SqlBuilder.setLimitThreshold(-1);
        }

        try{
            SqlBuilder.setLimitThreshold(2);
            new SqlBuilder().limit(1, 2);
            Assert.fail();
        } catch (IllegalArgumentException e){
            Assert.assertNotNull(e);
        } finally {
            SqlBuilder.setLimitThreshold(-1);
        }

        try{
            SqlBuilder.setLimitThreshold(2);
            new SqlBuilder().top(2);
            Assert.fail();
        } catch (IllegalArgumentException e){
            Assert.assertNotNull(e);
        } finally {
            SqlBuilder.setLimitThreshold(-1);
        }

        try{
            SqlBuilder.setLimitThreshold(2);
            new SqlBuilder().atPage(1, 2);
            Assert.fail();
        } catch (IllegalArgumentException e){
            Assert.assertNotNull(e);
        } finally {
            SqlBuilder.setLimitThreshold(-1);
        }

        try{
            SqlBuilder.setLimitThreshold(2);
            new SqlBuilder().limit(Parameter.integerOf("", 2));
            Assert.fail();
        } catch (IllegalArgumentException e){
            Assert.assertNotNull(e);
        } finally {
            SqlBuilder.setLimitThreshold(-1);
        }
    }

    @Test
    public void testLimitStartCountDef( ) {
        SqlBuilder sb = new SqlBuilder();
        sb.limit(ParameterDefinition.integerVar("start"), ParameterDefinition.integerVar("count"));
        assertEquals("LIMIT ?, ?", sb.build(ctx));
        List<ParameterDefinition> pl = sb.buildDefinitions();
        assertEquals(2, pl.size());
        assertX(pl.get(0), "start", JDBCType.INTEGER, false);
        assertX(pl.get(1), "count", JDBCType.INTEGER, false);
    }

    @Test
    public void testOffset( ) {
        SqlBuilder sb = new SqlBuilder();
        sb.offset(10, 20);
        assertEquals("OFFSET ? ROWS FETCH NEXT ? ROWS ONLY", sb.build(ctx));
        List<Parameter> pl = sb.buildParameters();
        assertEquals(2, pl.size());
        Parameter p = pl.get(0);
        assertX(pl.get(0), "", JDBCType.INTEGER, 10);
        assertX(pl.get(1), "", JDBCType.INTEGER, 20);
    }

    @Test
    public void testOffsetDef( ) {
        SqlBuilder sb = new SqlBuilder();
        sb.offset(ParameterDefinition.integerVar("start"), ParameterDefinition.integerVar("count"));
        assertEquals("OFFSET ? ROWS FETCH NEXT ? ROWS ONLY", sb.build(ctx));
        List<ParameterDefinition> pl = sb.buildDefinitions();
        assertEquals(2, pl.size());
        assertX(pl.get(0), "start", JDBCType.INTEGER, false);
        assertX(pl.get(1), "count", JDBCType.INTEGER, false);
    }

    @Test
    public void testTop( ) {
        SqlBuilder sb = new SqlBuilder();
        sb.top(10);
        assertEquals("TOP 10", sb.build(ctx));
        List<Parameter> pl = sb.buildParameters();
        assertEquals(0, pl.size());
    }
}
