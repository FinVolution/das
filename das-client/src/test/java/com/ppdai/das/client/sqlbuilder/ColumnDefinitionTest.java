package com.ppdai.das.client.sqlbuilder;

import static org.junit.Assert.assertEquals;

import java.sql.JDBCType;
import java.util.Optional;

import org.junit.Test;

import com.ppdai.das.client.ColumnDefinition;

public class ColumnDefinitionTest extends AbstractColumnTest {
    private static final BuilderContext bc = new DefaultBuilderContext();
    
    @Test
    public void testProperties( ) {
        ColumnDefinition column = Person.PERSON.CityID;
        assertEquals("CityID", column.getColumnName());
        assertEquals(JDBCType.INTEGER, column.getType());
        assertEquals(Optional.empty(), column.getAlias());
    }
    
    @Test
    public void testAlias( ) {
        ColumnDefinition column = Person.PERSON.CityID;
        ColumnDefinition columnAbc =  column.as("abc");
        assertEquals(Optional.empty(), column.getAlias());
        assertEquals("abc", columnAbc.getAlias().get());
    }

    @Test
    public void testBuild( ) {
        ColumnDefinition column = Person.PERSON.CityID;
        assertEquals("person.CityID", column.build(bc));
        assertEquals("person.CityID AS abc", column.as("abc").build(bc));

        column = Person.PERSON.as("P").CityID;
        assertEquals("P.CityID", column.build(bc));
        assertEquals("P.CityID AS abc", column.as("abc").build(bc));

    }

    @Override
    protected AbstractColumn column() {
        return Person.PERSON.as("T").CityID;
    }
    
    protected String getReferName() {
        return "T.CityID";
    }

    @Override
    protected JDBCType getColumnType() {
        return JDBCType.INTEGER;
    }

    @Override
    protected String getColumnName() {
        return "CityID";
    }
}
