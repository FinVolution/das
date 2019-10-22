package com.ppdai.das.client;

import com.ppdai.das.client.delegate.local.PPDaiDalParser;
import com.ppdai.das.client.sqlbuilder.BooleanExpression;
import com.ppdai.das.client.sqlbuilder.Bracket;
import com.ppdai.das.client.sqlbuilder.CustomizedColumn;
import com.ppdai.das.client.sqlbuilder.CustomizedExpression;
import com.ppdai.das.client.sqlbuilder.Keyword;
import com.ppdai.das.client.sqlbuilder.Operator;
import com.ppdai.das.client.sqlbuilder.Template;
import com.ppdai.das.client.sqlbuilder.Text;
import com.ppdai.das.core.client.DalParser;

import java.sql.JDBCType;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public interface SegmentConstants {
    ParameterDefinition VAR = ParameterDefinition.builder().name("VAR").build();

    Text COMMA = new Text(",");
    
    Text SPACE = new Text(" ");
    
    Text PLACE_HOLDER = new Text("?");

    Keyword SELECT = new Keyword("SELECT");
    
    Keyword INSERT = new Keyword("INSERT");
    
    Keyword DELETE = new Keyword("DELETE");
    
    Keyword UPDATE = new Keyword("UPDATE");
    
    Keyword DISTINCT = new Keyword("DISTINCT");

    Keyword FROM = new Keyword("FROM");
    
    Keyword INTO = new Keyword("INTO");
    
    Keyword VALUES = new Keyword("VALUES");
    
    Keyword SET = new Keyword("SET");

    Keyword AS = new Keyword("AS");

    Keyword JOIN = new Keyword("JOIN");
    
    Keyword INNER_JOIN = new Keyword("INNER JOIN");
    
    Keyword FULL_JOIN = new Keyword("FULL JOIN");

    Keyword LEFT_JOIN = new Keyword("LEFT JOIN");

    Keyword RIGHT_JOIN = new Keyword("RIGHT JOIN");

    Keyword CROSS_JOIN = new Keyword("CROSS JOIN");
    
    Keyword ON = new Keyword("ON");
    
    Keyword USING = new Keyword("USING");
    
    Keyword WHERE= new Keyword("WHERE");
    
    Keyword ORDER_BY = new Keyword("ORDER BY");

    Keyword ASC = new Keyword("ASC");

    Keyword DESC = new Keyword("DESC");

    Keyword GROUP_BY = new Keyword("GROUP BY");

    Keyword HAVING = new Keyword("HAVING");
    
    Keyword UNION =  new Keyword("UNION");

    Keyword UNION_ALL =  new Keyword("UNION ALL");
    
    Operator AND = new Operator("AND");
    
    Operator OR = new Operator("OR");
    
    Operator NOT = new Operator("NOT");

    Bracket leftBracket = new Bracket(true);

    Bracket rightBracket = new Bracket(false);
    
    BooleanExpression TRUE = BooleanExpression.TRUE;
    
    BooleanExpression FALSE = BooleanExpression.FALSE;

    //MS SqlSevre special keyword
    Keyword WITH_NO_LOCK = new Keyword("WITH (NOLOCK)");
    
    static Text text(String text) {
        return new Text(text);
    }
    
    static CustomizedColumn column(String expr) {
        return new CustomizedColumn(expr);
    }
    
    static CustomizedExpression expression(String expr) {
        return new CustomizedExpression(expr);
    }
    
    /**
     * Work with column type
     * @param name
     * @return
     */
    static ParameterDefinition.Builder var(String name) {
        return ParameterDefinition.builder().name(name);
    }

    static ParameterDefinition var(JDBCType type) {
        return ParameterDefinition.builder().type(type).name(VAR.getName()).build();
    }

    static ParameterDefinition var(String name, JDBCType type) {
        return ParameterDefinition.var(name, type);
    }
    
    static ParameterDefinition inVar(String name, JDBCType type) {
        return ParameterDefinition.builder().name(name).type(type).inValues(true).build();
    }

    static ParameterDefinition inVar(JDBCType type) {
        return inVar(VAR.getName(), type);
    }

    static Template template(String template, Parameter... parameters) {
        return new Template(template, parameters);
    }

    static Template template(String template, ParameterDefinition... parameterDefinitions) {
        return new Template(template, parameterDefinitions);
    }

    static Parameter set(String name, JDBCType type, Object value) {
        return new Parameter(name, type, value);
    }

    static Parameter set(JDBCType type, Object value) {
        return set("", type, value);
    }

    static Parameter set(String name, JDBCType type, List<?> values) {
        return new Parameter(name, type, values);
    }

    static Parameter set(JDBCType type, List<?> values) {
        return set("", type, values);
    }

    /**
     * You can import this method to build segments in bracket in the fly
     */
    static Object[] bracket(Object... segs) {
        List<Object> l = new ArrayList<>();
        l.add(leftBracket);

        for(Object seg: segs)
            add(l, seg);

        l.add(rightBracket);
        return l.toArray(new Object[segs.length + 2]);
    }

    static Object[] concatWith(Text separator, Object... segs) {
        LinkedList<Object> l = new LinkedList<>();
        for(Object seg: segs) {
            add(l, seg);
            l.add(separator);
        }

        if(segs.length > 0) l.removeLast();
        return l.toArray();
    }

    static Object[] comma(Object... segs) {
        return concatWith(COMMA, segs);
    }

    static Object[] allOf(Object... exps) {
        return bracket(concatWith(AND, (Object[])exps));
    }

    static Object[] anyOf(Object... exps) {
        return bracket(concatWith(OR, (Object[])exps));
    }
    
    static void add(List<Object> list, Object value) {
        if(value instanceof Object[]) {
            Object[] values = (Object[])value;
            for(Object val: values ) {
                add(list, val);
            }
        }else
            list.add(value);
    }

    static Object[] match(TableDefinition table, Object sample, DalParser parser) throws SQLException {
        Map<String, ?> fields = parser.getFields(sample);

        for (String columnName : parser.getColumnNames())
            if (fields.get(columnName) == null)
                fields.remove(columnName);

        if (fields.isEmpty())
            throw new IllegalArgumentException("All fields in sample are null");

        List<Object> conditions = new ArrayList<>();
        for(String columnName: fields.keySet()) {
            conditions.add(table.getColumnDefinition(columnName).eq(fields.get(columnName)));
            conditions.add(SegmentConstants.AND);
        }

        conditions.remove(conditions.size() -1);

        return conditions.toArray(new Object[conditions.size()]);
    }

    static Object[] match(TableDefinition table, Object sample) throws SQLException {
        return match(table, sample, new PPDaiDalParser(sample.getClass()));
     }
}
