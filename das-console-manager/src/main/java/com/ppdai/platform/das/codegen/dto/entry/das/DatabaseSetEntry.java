package com.ppdai.platform.das.codegen.dto.entry.das;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ppdai.das.client.ColumnDefinition;
import com.ppdai.das.client.TableDefinition;
import com.ppdai.platform.das.codegen.common.validates.group.dbSet.AddDbSetEntry;
import com.ppdai.platform.das.codegen.common.validates.group.dbSet.DeleteDbSetEntry;
import com.ppdai.platform.das.codegen.common.validates.group.dbSet.UpdateDbSetEntry;
import com.ppdai.platform.das.codegen.dto.view.search.CheckTypes;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.sql.JDBCType;
import java.util.Date;

/**
 * create by das-console
 * 请勿修改此文件
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table
public class DatabaseSetEntry {

    public static final DatabasesetentryDefinition DATABASESETENTRY = new DatabasesetentryDefinition();

    public static class DatabasesetentryDefinition extends TableDefinition {
            public final ColumnDefinition id;
            public final ColumnDefinition name;
            public final ColumnDefinition databaseType;
            public final ColumnDefinition sharding;
            public final ColumnDefinition dbId;
            public final ColumnDefinition dbsetId;
            public final ColumnDefinition updateUserNo;
            public final ColumnDefinition insertTime;
            public final ColumnDefinition updateTime;
        
        public DatabasesetentryDefinition as(String alias) {
            return _as(alias);
        }
        public DatabasesetentryDefinition inShard(String shardId) {
            return _inShard(shardId);
        }

        public DatabasesetentryDefinition shardBy(String shardValue) {
            return _shardBy(shardValue);
        }

        public DatabasesetentryDefinition() {
            super("databasesetentry");
					id = column("id", JDBCType.BIGINT);
					name = column("name", JDBCType.VARCHAR);
					databaseType = column("database_type", JDBCType.TINYINT);
					sharding = column("sharding", JDBCType.VARCHAR);
					dbId = column("db_Id", JDBCType.INTEGER);
					dbsetId = column("dbset_id", JDBCType.INTEGER);
					updateUserNo = column("update_user_no", JDBCType.VARCHAR);
					insertTime = column("insert_time", JDBCType.TIMESTAMP);
					updateTime = column("update_time", JDBCType.TIMESTAMP);
		            setColumnDefinitions(
                        id, name, databaseType, sharding, dbId, dbsetId, updateUserNo, insertTime, 
                        updateTime
            );
        }
    }

	@NotNull(message = "{databaseSetEntry.id.notNull}", groups = {UpdateDbSetEntry.class, DeleteDbSetEntry.class})
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@NotBlank(message = "{databaseSetEntry.name.notNull}", groups = {AddDbSetEntry.class, UpdateDbSetEntry.class})
	@Column(name = "name")
	private String name;

    /** 1.Master 2.Slave **/
	@NotNull(message = "{databaseSetEntry.databaseType.notNull}", groups = {AddDbSetEntry.class, UpdateDbSetEntry.class})
	@Column(name = "database_type")
	private Integer databaseType;

	@Column(name = "sharding")
	private String sharding;

    /** 物理数据ID **/
	@NotNull(message = "{databaseSetEntry.db_Id.notNull}", groups = {AddDbSetEntry.class, UpdateDbSetEntry.class})
	@Column(name = "db_Id")
	private Long db_Id;

    /** 逻辑数据库ID **/
	@Column(name = "dbset_id")
	private Long dbset_id;

	@Column(name = "update_user_no")
	private String update_user_no;

	@Column(name = "insert_time")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date create_time;

	@Column(name = "update_time")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date update_time;

	private String db_catalog;

	private String providerName;

	private String userName;

	private String password;

	private String dbAddress;

	private String dbPort;

	private String connectionString;

	private String dbsetName;

	private String dbName;

	@NotNull(message = "{databaseSetEntry.groupId.notNull}", groups = {AddDbSetEntry.class, UpdateDbSetEntry.class, DeleteDbSetEntry.class})
	private Long groupId;

	private CheckTypes databaseTypes;

}