package com.ppdai.das.client.sqlbuilder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.sql.JDBCType;

import org.junit.Test;

public class ColumnTest extends AbstractColumnTest {
    @Test
    public void testCreateTypeAlias( ) {
        Table table = new Table("t");
        Column c = new Column(table, "col", JDBCType.VARBINARY);
        assertEquals("col", c.getColumnName());
        assertFalse(c.getAlias().isPresent());
        c.as("c");
        assertTrue(c.getAlias().isPresent());
        
        assertEquals(JDBCType.VARBINARY, c.getType());
        assertEquals("t.col AS c", c.build(new DefaultBuilderContext()));
    }

    @Test
    public void testReferenceName( ) {
        Table table = new Table("t");
        Column c = new Column(table, "col", JDBCType.VARBINARY);
        c.as("c");
        
        assertEquals("t.col", c.getReference(new DefaultBuilderContext()));
    }
    
    
    @Override
    protected AbstractColumn column() {
        return new Table("table").as("T").column("column", type).as("c");
    }

    @Override
    protected String getReferName() {
        return "T.column";
    }

    @Override
    protected JDBCType getColumnType() {
        return JDBCType.ARRAY;
    }

    @Override
    protected String getColumnName() {
        return "column";
    }
}
