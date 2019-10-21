package com.ppdai.platform.das.codegen.dto.entry.das;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ppdai.das.client.ColumnDefinition;
import com.ppdai.das.client.TableDefinition;
import com.ppdai.platform.das.codegen.common.validates.group.dbSet.AddDbSet;
import com.ppdai.platform.das.codegen.common.validates.group.dbSet.DeleteDbSet;
import com.ppdai.platform.das.codegen.common.validates.group.dbSet.UpdateDbSet;
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
public class DatabaseSet {

    public static final DatabasesetDefinition DATABASESET = new DatabasesetDefinition();

    public static class DatabasesetDefinition extends TableDefinition {
            public final ColumnDefinition id;
            public final ColumnDefinition name;
            public final ColumnDefinition dbType;
            public final ColumnDefinition strategyType;
            public final ColumnDefinition className;
            public final ColumnDefinition strategySource;
            public final ColumnDefinition groupId;
            public final ColumnDefinition dynamicStrategyId;
            public final ColumnDefinition updateUserNo;
            public final ColumnDefinition insertTime;
            public final ColumnDefinition updateTime;
        
        public DatabasesetDefinition as(String alias) {
            return _as(alias);
        }
        public DatabasesetDefinition inShard(String shardId) {
            return _inShard(shardId);
        }

        public DatabasesetDefinition shardBy(String shardValue) {
            return _shardBy(shardValue);
        }

        public DatabasesetDefinition() {
            super("databaseset");
					id = column("id", JDBCType.BIGINT);
					name = column("name", JDBCType.VARCHAR);
					dbType = column("db_type", JDBCType.TINYINT);
					strategyType = column("strategy_type", JDBCType.TINYINT);
					className = column("class_name", JDBCType.VARCHAR);
					strategySource = column("strategy_source", JDBCType.LONGVARCHAR);
					groupId = column("group_id", JDBCType.BIGINT);
					dynamicStrategyId = column("dynamic_strategy_id", JDBCType.INTEGER);
					updateUserNo = column("update_user_no", JDBCType.VARCHAR);
					insertTime = column("insert_time", JDBCType.TIMESTAMP);
					updateTime = column("update_time", JDBCType.TIMESTAMP);
		            setColumnDefinitions(
                        id, name, dbType, strategyType, className, strategySource, groupId, 
                        dynamicStrategyId, updateUserNo, insertTime, updateTime
            );
        }
    }

	@NotNull(message = "{databaseSet.id.notNull}", groups = {UpdateDbSet.class, DeleteDbSet.class})
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@NotBlank(message = "{databaseSet.name.notNull}", groups = {AddDbSet.class, UpdateDbSet.class})
	@Column(name = "name")
	private String name;

    /** 数据库类型：1、mysql 2、SqlServer **/
	@NotNull(message = "{databaseSet.db_type.notNull}", groups = {AddDbSet.class, UpdateDbSet.class})
	@Column(name = "db_type")
	private Integer dbType;

    /** 类型：0.无策略 1、私有策略 2、公共策略 **/
	@Column(name = "strategy_type")
	private Integer strategyType;

	@Column(name = "class_name")
	private String className;

	@Column(name = "strategy_source")
	private String strategySource;

	@NotNull(message = "{databaseSet.groupId.notNull}", groups = {AddDbSet.class, UpdateDbSet.class, DeleteDbSet.class})
	@Column(name = "group_id")
	private Long groupId;

    /** 这字段指向公有策略，对应public_sharding_strategy.id **/
	@Column(name = "dynamic_strategy_id")
	private Long dynamicStrategyId;

	@Column(name = "update_user_no")
	private String updateUserNo;

	@Column(name = "insert_time")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date create_time;

	@Column(name = "update_time")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date update_time;

	private String groupName;

	private CheckTypes dbTypes;

	private CheckTypes strategyTypes;

	private String app_id;
}