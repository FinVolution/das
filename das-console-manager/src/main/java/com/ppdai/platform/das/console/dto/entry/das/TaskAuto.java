package com.ppdai.platform.das.console.dto.entry.das;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ppdai.das.client.ColumnDefinition;
import com.ppdai.das.client.TableDefinition;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
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
public class TaskAuto {

    public static final TaskAutoDefinition TASKAUTO = new TaskAutoDefinition();

    public static class TaskAutoDefinition extends TableDefinition {
            public final ColumnDefinition id;
            public final ColumnDefinition projectId;
            public final ColumnDefinition dbName;
            public final ColumnDefinition tableName;
            public final ColumnDefinition className;
            public final ColumnDefinition methodName;
            public final ColumnDefinition sqlStyle;
            public final ColumnDefinition crudType;
            public final ColumnDefinition fields;
            public final ColumnDefinition whereCondition;
            public final ColumnDefinition sqlContent;
            public final ColumnDefinition generated;
            public final ColumnDefinition version;
            public final ColumnDefinition updateUserNo;
            public final ColumnDefinition comment;
            public final ColumnDefinition scalarType;
            public final ColumnDefinition pagination;
            public final ColumnDefinition orderby;
            public final ColumnDefinition approved;
            public final ColumnDefinition approveMsg;
            public final ColumnDefinition hints;
            public final ColumnDefinition insertTime;
            public final ColumnDefinition updateTime;
        
        public TaskAutoDefinition as(String alias) {
            return _as(alias);
        }
        public TaskAutoDefinition inShard(String shardId) {
            return _inShard(shardId);
        }

        public TaskAutoDefinition shardBy(String shardValue) {
            return _shardBy(shardValue);
        }

        public TaskAutoDefinition() {
            super("task_auto");
					id = column("id", JDBCType.BIGINT);
					projectId = column("project_id", JDBCType.INTEGER);
					dbName = column("db_name", JDBCType.VARCHAR);
					tableName = column("table_name", JDBCType.VARCHAR);
					className = column("class_name", JDBCType.VARCHAR);
					methodName = column("method_name", JDBCType.VARCHAR);
					sqlStyle = column("sql_style", JDBCType.VARCHAR);
					crudType = column("crud_type", JDBCType.VARCHAR);
					fields = column("fields", JDBCType.LONGVARCHAR);
					whereCondition = column("where_condition", JDBCType.LONGVARCHAR);
					sqlContent = column("sql_content", JDBCType.LONGVARCHAR);
					generated = column("generated", JDBCType.BIT);
					version = column("version", JDBCType.INTEGER);
					updateUserNo = column("update_user_no", JDBCType.VARCHAR);
					comment = column("comment", JDBCType.LONGVARCHAR);
					scalarType = column("scalarType", JDBCType.VARCHAR);
					pagination = column("pagination", JDBCType.BIT);
					orderby = column("orderby", JDBCType.VARCHAR);
					approved = column("approved", JDBCType.INTEGER);
					approveMsg = column("approveMsg", JDBCType.LONGVARCHAR);
					hints = column("hints", JDBCType.VARCHAR);
					insertTime = column("insert_time", JDBCType.TIMESTAMP);
					updateTime = column("update_time", JDBCType.TIMESTAMP);
		            setColumnDefinitions(
                        id, projectId, dbName, tableName, className, methodName, sqlStyle, crudType, 
                        fields, whereCondition, sqlContent, generated, version, updateUserNo, comment, 
                        scalarType, pagination, orderby, approved, approveMsg, hints, insertTime, 
                        updateTime
            );
        }
    }


	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "project_id")
	private Integer project_id;

	@Column(name = "db_name")
	private String databaseSetName;

	@Column(name = "table_name")
	private String table_name;

	@Column(name = "class_name")
	private String class_name;

	@Column(name = "method_name")
	private String method_name;

	@Column(name = "sql_style")
	private String sql_style;

	@Column(name = "crud_type")
	private String crud_type;

	@Column(name = "fields")
	private String fields;

	@Column(name = "where_condition")
	private String condition;

	@Column(name = "sql_content")
	private String sql_content;

	@Column(name = "generated")
	private Boolean generated;

	@Column(name = "version")
	private Integer version;

	@Column(name = "update_user_no")
	private String update_user_no;

	@Column(name = "comment")
	private String comment;

	@Column(name = "scalarType")
	private String scalarType;

	@Column(name = "pagination")
	private Boolean pagination;

	@Column(name = "orderby")
	private String orderby;

	@Column(name = "approved")
	private Integer approved;

	@Column(name = "approveMsg")
	private String approveMsg;

	@Column(name = "hints")
	private String hints;

	@Column(name = "insert_time")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date insertTime;

	@Column(name = "update_time")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date update_time;

	private Long alldbs_id;

	private String str_update_time;

	private String str_approved;
}