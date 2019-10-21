package com.ppdai.platform.das.codegen.dto.entry.das;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ppdai.das.client.ColumnDefinition;
import com.ppdai.das.client.TableDefinition;
import com.ppdai.platform.das.codegen.common.validates.group.selectEntity.AddSelectEntity;
import com.ppdai.platform.das.codegen.common.validates.group.selectEntity.DeleteSelectEntity;
import com.ppdai.platform.das.codegen.common.validates.group.selectEntity.UpdateSelectEntity;
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
public class TaskSql {

    public static final TaskSqlDefinition TASKSQL = new TaskSqlDefinition();

    public static class TaskSqlDefinition extends TableDefinition {
            public final ColumnDefinition id;
            public final ColumnDefinition className;
            public final ColumnDefinition pojoName;
            public final ColumnDefinition methodName;
            public final ColumnDefinition crudType;
            public final ColumnDefinition sqlContent;
            public final ColumnDefinition projectId;
            public final ColumnDefinition parameters;
            public final ColumnDefinition generated;
            public final ColumnDefinition version;
            public final ColumnDefinition updateUserNo;
            public final ColumnDefinition updateTime;
            public final ColumnDefinition comment;
            public final ColumnDefinition scalarType;
            public final ColumnDefinition pojoType;
            public final ColumnDefinition pagination;
            public final ColumnDefinition sqlStyle;
            public final ColumnDefinition approved;
            public final ColumnDefinition approveMsg;
            public final ColumnDefinition hints;
            public final ColumnDefinition dbsetId;
            public final ColumnDefinition fieldType;
        
        public TaskSqlDefinition as(String alias) {
            return _as(alias);
        }
        public TaskSqlDefinition inShard(String shardId) {
            return _inShard(shardId);
        }

        public TaskSqlDefinition shardBy(String shardValue) {
            return _shardBy(shardValue);
        }

        public TaskSqlDefinition() {
            super("task_sql");
					id = column("id", JDBCType.BIGINT);
					className = column("class_name", JDBCType.VARCHAR);
					pojoName = column("pojo_name", JDBCType.VARCHAR);
					methodName = column("method_name", JDBCType.VARCHAR);
					crudType = column("crud_type", JDBCType.VARCHAR);
					sqlContent = column("sql_content", JDBCType.LONGVARCHAR);
					projectId = column("project_id", JDBCType.INTEGER);
					parameters = column("parameters", JDBCType.LONGVARCHAR);
					generated = column("generated", JDBCType.BIT);
					version = column("version", JDBCType.INTEGER);
					updateUserNo = column("update_user_no", JDBCType.VARCHAR);
					updateTime = column("update_time", JDBCType.TIMESTAMP);
					comment = column("comment", JDBCType.LONGVARCHAR);
					scalarType = column("scalarType", JDBCType.VARCHAR);
					pojoType = column("pojoType", JDBCType.VARCHAR);
					pagination = column("pagination", JDBCType.BIT);
					sqlStyle = column("sql_style", JDBCType.VARCHAR);
					approved = column("approved", JDBCType.INTEGER);
					approveMsg = column("approveMsg", JDBCType.LONGVARCHAR);
					hints = column("hints", JDBCType.VARCHAR);
					dbsetId = column("dbset_id", JDBCType.INTEGER);
					fieldType = column("field_type", JDBCType.INTEGER);
		            setColumnDefinitions(
                        id, className, pojoName, methodName, crudType, sqlContent, projectId, 
                        parameters, generated, version, updateUserNo, updateTime, comment, scalarType, 
                        pojoType, pagination, sqlStyle, approved, approveMsg, hints, dbsetId, fieldType
            );
        }
    }

	@NotNull(message = "{genTaskByFreeSql.id.notNull}", groups = {UpdateSelectEntity.class, DeleteSelectEntity.class})
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@NotBlank(message = "{genTaskByFreeSql.class_name.notNull}", groups = {AddSelectEntity.class, UpdateSelectEntity.class})
	@Column(name = "class_name")
	private String class_name;

	@Column(name = "pojo_name")
	private String pojo_name;

	@Column(name = "method_name")
	private String method_name;

	@Column(name = "crud_type")
	private String crud_type;

	@NotNull(message = "{genTaskByFreeSql.sql_content.notNull}", groups = {AddSelectEntity.class, UpdateSelectEntity.class})
	@Column(name = "sql_content")
	private String sql_content;

	@NotNull(message = "{genTaskByFreeSql.project_id.notNull}", groups = {AddSelectEntity.class, UpdateSelectEntity.class})
	@Column(name = "project_id")
	private Long project_id;

	@Column(name = "parameters")
	private String parameters;

	@Column(name = "generated")
	private Boolean generated;

	@Column(name = "version")
	private Integer version;

	@Column(name = "update_user_no")
	private String update_user_no;

	@Column(name = "update_time")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date update_time;

	@Column(name = "comment")
	private String comment;

	@Column(name = "scalarType")
	private String scalarType;

	@Column(name = "pojoType")
	private String pojoType;

	@Column(name = "pagination")
	private Boolean pagination;

	@Column(name = "sql_style")
	private String sql_style;

	@Column(name = "approved")
	private Integer approved;

	@Column(name = "approveMsg")
	private String approveMsg;

	@Column(name = "hints")
	private String hints;

    /** databaseset id **/
	@NotNull(message = "{genTaskByFreeSql.dbset_id.notNull}", groups = {AddSelectEntity.class, UpdateSelectEntity.class})
	@Column(name = "dbset_id")
	private Integer dbset_id;

    /** 字段类型 **/
	@Column(name = "field_type")
	private Integer field_type;

	private Integer alldbs_id;

	private String str_approved;

	private String str_update_time;

	private String dbsetName;
}