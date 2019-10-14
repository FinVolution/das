package com.ppdai.das.client.sqlbuilder;

import static org.junit.Assert.assertEquals;

import java.sql.JDBCType;

import org.junit.Test;

public class TableTest {
    @Test
    public void testName( ) {
        Table table = new Table("t");
        assertEquals("t", table.getName());
    }
    @Test
    public void testNameAndAlias( ) {
        Table table = new Table("t", "al");
        assertEquals("t", table.getName());
        assertEquals("al", table.getAlias());
    }

    @Test
    public void testInShard( ) {
        Table table = new Table("t");
        table.inShard("1");
        assertEquals("1", table.getShardId());
    }

    @Test
    public void testShardValue( ) {
        Table table = new Table("t").shardBy("123");
        assertEquals("123", table.getShardValue());
    }

    @Test
    public void testReferenceName() {
        Table table = new Table("t").as("ali");
        assertEquals("ali", table.getReferName(new DefaultBuilderContext()));
    }

    @Test
    public void testBuild() {
        Table table = new Table("t").as("ali");
        assertEquals("t ali", table.build(new DefaultBuilderContext()));
    }
    
    @Test
    public void testAllColumns() {
        Table table = new Table("t").as("ali");
        table.bigintColumn("aaa");
        table.charColumn("bbb");
        Column[] all = table.allColumns();
        assertEquals("aaa", all[0].getColumnName());
        assertEquals(JDBCType.BIGINT, all[0].getType());
        assertEquals("bbb", all[1].getColumnName());
        assertEquals(JDBCType.CHAR, all[1].getType());
    }
    
    @Test
    public void testColumn() {
        Table table = new Table("t").as("ali");
        Column c = table.column("name", JDBCType.VARBINARY);        
        assertEquals("name", c.getColumnName());
        assertEquals(JDBCType.VARBINARY, c.getType());
    }

    @Test
    public void testXColumn() {
        Table table = new Table("t").as("ali");
        Column c = table.bigintColumn("bigintColumn");        
        assertEquals("bigintColumn", c.getColumnName());
        assertEquals(JDBCType.BIGINT, c.getType());
        
        c = table.binaryColumn("binaryColumn");        
        assertEquals("binaryColumn", c.getColumnName());
        assertEquals(JDBCType.BINARY, c.getType());
        
        c = table.bitColumn("bitColumn");        
        assertEquals("bitColumn", c.getColumnName());
        assertEquals(JDBCType.BIT, c.getType());

        c = table.blobColumn("blobColumn");        
        assertEquals("blobColumn", c.getColumnName());
        assertEquals(JDBCType.BLOB, c.getType());

        c = table.booleanColumn("booleanColumn");        
        assertEquals("booleanColumn", c.getColumnName());
        assertEquals(JDBCType.BOOLEAN, c.getType());

        c = table.clobColumn("cblobColumn");        
        assertEquals("cblobColumn", c.getColumnName());
        assertEquals(JDBCType.CLOB, c.getType());

        c = table.charColumn("charColumn");        
        assertEquals("charColumn", c.getColumnName());
        assertEquals(JDBCType.CHAR, c.getType());

        c = table.dateColumn("dateColumn");        
        assertEquals("dateColumn", c.getColumnName());
        assertEquals(JDBCType.DATE, c.getType());

        c = table.decimalColumn("decimalColumn");        
        assertEquals("decimalColumn", c.getColumnName());
        assertEquals(JDBCType.DECIMAL, c.getType());

        c = table.doubleColumn("doubleColumn");        
        assertEquals("doubleColumn", c.getColumnName());
        assertEquals(JDBCType.DOUBLE, c.getType());

        c = table.floatColumn("floatColumn");        
        assertEquals("floatColumn", c.getColumnName());
        assertEquals(JDBCType.FLOAT, c.getType());

        c = table.integerColumn("integerColumn");        
        assertEquals("integerColumn", c.getColumnName());
        assertEquals(JDBCType.INTEGER, c.getType());

        c = table.longnvarcharColumn("longnvarcharColumn");        
        assertEquals("longnvarcharColumn", c.getColumnName());
        assertEquals(JDBCType.LONGNVARCHAR, c.getType());

        c = table.longvarbinaryColumn("longvarbinaryColumn");        
        assertEquals("longvarbinaryColumn", c.getColumnName());
        assertEquals(JDBCType.LONGVARBINARY, c.getType());

        c = table.longvarcharColumn("longvarcharColumn");        
        assertEquals("longvarcharColumn", c.getColumnName());
        assertEquals(JDBCType.LONGVARCHAR, c.getType());

        c = table.ncharColumn("ncharColumn");        
        assertEquals("ncharColumn", c.getColumnName());
        assertEquals(JDBCType.NCHAR, c.getType());

        c = table.nclobColumn("nclobColumn");        
        assertEquals("nclobColumn", c.getColumnName());
        assertEquals(JDBCType.NCLOB, c.getType());

        c = table.nullColumn("nullColumn");        
        assertEquals("nullColumn", c.getColumnName());
        assertEquals(JDBCType.NULL, c.getType());

        c = table.numericColumn("numericColumn");        
        assertEquals("numericColumn", c.getColumnName());
        assertEquals(JDBCType.NUMERIC, c.getType());

        c = table.nvarcharColumn("nvarcharColumn");        
        assertEquals("nvarcharColumn", c.getColumnName());
        assertEquals(JDBCType.NVARCHAR, c.getType());

        c = table.realColumn("realColumn");        
        assertEquals("realColumn", c.getColumnName());
        assertEquals(JDBCType.REAL, c.getType());

        c = table.smallintColumn("smallintColumn");        
        assertEquals("smallintColumn", c.getColumnName());
        assertEquals(JDBCType.SMALLINT, c.getType());

        c = table.sqlxmlColumn("sqlxmlColumn");        
        assertEquals("sqlxmlColumn", c.getColumnName());
        assertEquals(JDBCType.SQLXML, c.getType());

        c = table.timeColumn("timeColumn");        
        assertEquals("timeColumn", c.getColumnName());
        assertEquals(JDBCType.TIME, c.getType());

        c = table.timestampColumn("timestampColumn");        
        assertEquals("timestampColumn", c.getColumnName());
        assertEquals(JDBCType.TIMESTAMP, c.getType());

        c = table.timestampWithTimezoneColumn("timestampWithTimezoneColumn");        
        assertEquals("timestampWithTimezoneColumn", c.getColumnName());
        assertEquals(JDBCType.TIMESTAMP_WITH_TIMEZONE, c.getType());

        c = table.timeWithTimezoneColumn("timeWithTimezoneColumn");        
        assertEquals("timeWithTimezoneColumn", c.getColumnName());
        assertEquals(JDBCType.TIME_WITH_TIMEZONE, c.getType());

        c = table.tinyintColumn("tinyintColumn");        
        assertEquals("tinyintColumn", c.getColumnName());
        assertEquals(JDBCType.TINYINT, c.getType());

        c = table.varbinaryColumn("varbinaryColumn");        
        assertEquals("varbinaryColumn", c.getColumnName());
        assertEquals(JDBCType.VARBINARY, c.getType());

        c = table.varcharColumn("varcharColumn");        
        assertEquals("varcharColumn", c.getColumnName());
        assertEquals(JDBCType.VARCHAR, c.getType());
    }
}
