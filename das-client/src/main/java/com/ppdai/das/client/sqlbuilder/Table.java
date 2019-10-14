package com.ppdai.das.client.sqlbuilder;

import java.sql.JDBCType;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import com.ppdai.das.client.Segment;

/**
 * Represent normal table or sharded logic table.
 * 
 * IMPORTANT NOTE:
 * 
 * In case of shard table, you need to get the table or column segment 
 * out of the builder and do your own shard compution to get the correct real table name 
 * 
 * Also work as factory to create column of the table with given JDBC type.
 * 
 * @author hejiehui
 *
 */
public class Table implements TableReference, Segment{
    private String name;
    private String alias;
    private String shardId;
    private String shardValue;
    private Map<String, Column> columns = new LinkedHashMap<>();
    
    public Table(String name) {
        this.name = Objects.requireNonNull(name);
    }
    
    public Table(String name, String alias) {
        this(name);
        this.alias = alias;
    }
    
    public Table inShard(String shardId) {
        this.shardId = shardId;
        return this;
    }
    
    public Table shardBy(String shardValue) {
        this.shardValue = shardValue;
        return this;
    }
    
    public Table as(String alias) {
        if(this.alias != null)
            throw new IllegalArgumentException("Table ca not be alias twice.");

        this.alias = alias;
        return this;
    }
    
    public String getName() {
        return name;
    }

    public String getAlias() {
        return alias;
    }

    public String getShardId() {
        return shardId;
    }

    public String getShardValue() {
        return shardValue;
    }
    
    public String getReferName(BuilderContext helper) {
        return alias == null ? helper.locateTableName(this) : alias;
    }
    
    @Override
    public String build(BuilderContext helper) {
        String realName = helper.locateTableName(this);
        return alias == null ? realName : realName + " " + alias;
    }

    public Column[] allColumns() {
        return columns.values().toArray(new Column[]{});
    }

    public Column column(String name, JDBCType type) {
        if(columns.containsKey(name))
            throw new IllegalArgumentException("Duplicate name detected.");

        Column newCol = new Column(this, name, type);
        columns.put(name, newCol);
        return newCol;
    }
    
    public String toString() {
        return build(new DefaultBuilderContext());
    }
    
    public Column bitColumn(String name) {return column(name, JDBCType.BIT);}
    
    public Column tinyintColumn(String name) {return column(name, JDBCType.TINYINT);}
    
    public Column smallintColumn(String name) {return column(name, JDBCType.SMALLINT);}
    
    public Column integerColumn(String name) {return column(name, JDBCType.INTEGER);}
    
    public Column bigintColumn(String name) {return column(name, JDBCType.BIGINT);}
    
    public Column floatColumn(String name) {return column(name, JDBCType.FLOAT);}
    
    public Column realColumn(String name) {return column(name, JDBCType.REAL);}
    
    public Column doubleColumn(String name) {return column(name, JDBCType.DOUBLE);}
    
    public Column numericColumn(String name) {return column(name, JDBCType.NUMERIC);}
    
    public Column decimalColumn(String name) {return column(name, JDBCType.DECIMAL);}
    
    public Column charColumn(String name) {return column(name, JDBCType.CHAR);}
    
    public Column varcharColumn(String name) {return column(name, JDBCType.VARCHAR);}
    
    public Column longvarcharColumn(String name) {return column(name, JDBCType.LONGVARCHAR);}
    
    public Column dateColumn(String name) {return column(name, JDBCType.DATE);}
    
    public Column timeColumn(String name) {return column(name, JDBCType.TIME);}
    
    public Column timestampColumn(String name) {return column(name, JDBCType.TIMESTAMP);}
    
    public Column binaryColumn(String name) {return column(name, JDBCType.BINARY);}
    
    public Column varbinaryColumn(String name) {return column(name, JDBCType.VARBINARY);}
    
    public Column longvarbinaryColumn(String name) {return column(name, JDBCType.LONGVARBINARY);}
    
    public Column nullColumn(String name) {return column(name, JDBCType.NULL);}
    
    public Column blobColumn(String name) {return column(name, JDBCType.BLOB);}
    
    public Column clobColumn(String name) {return column(name, JDBCType.CLOB);}
    
    public Column booleanColumn(String name) {return column(name, JDBCType.BOOLEAN);}
    
    public Column ncharColumn(String name) {return column(name, JDBCType.NCHAR);}
    
    public Column nvarcharColumn(String name) {return column(name, JDBCType.NVARCHAR);}
    
    public Column longnvarcharColumn(String name) {return column(name, JDBCType.LONGNVARCHAR);}
    
    public Column nclobColumn(String name) {return column(name, JDBCType.NCLOB);}
    
    public Column sqlxmlColumn(String name) {return column(name, JDBCType.SQLXML);}
    
    public Column timeWithTimezoneColumn(String name) {return column(name, JDBCType.TIME_WITH_TIMEZONE);}
    
    public Column timestampWithTimezoneColumn(String name) {return column(name, JDBCType.TIMESTAMP_WITH_TIMEZONE);}
}