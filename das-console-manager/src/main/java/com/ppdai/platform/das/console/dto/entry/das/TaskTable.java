package com.ppdai.platform.das.console.dto.entry.das;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ppdai.das.client.ColumnDefinition;
import com.ppdai.das.client.TableDefinition;
import com.ppdai.platform.das.console.common.validates.group.tableEntity.AddTableEntity;
import com.ppdai.platform.das.console.common.validates.group.tableEntity.DeleteTableEntity;
import com.ppdai.platform.das.console.common.validates.group.tableEntity.UpdateTableEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Min;
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
public class TaskTable {

    public static final TaskTableDefinition TASKTABLE = new TaskTableDefinition();

    public static class TaskTableDefinition extends TableDefinition {
            public final ColumnDefinition id;
            public final ColumnDefinition projectId;
            public final ColumnDefinition tableNames;
            public final ColumnDefinition viewNames;
            public final ColumnDefinition customTableName;
            public final ColumnDefinition prefix;
            public final ColumnDefinition suffix;
            public final ColumnDefinition cudBySp;
            public final ColumnDefinition pagination;
            public final ColumnDefinition generated;
            public final ColumnDefinition version;
            public final ColumnDefinition updateUserNo;
            public final ColumnDefinition updateTime;
            public final ColumnDefinition comment;
            public final ColumnDefinition sqlStyle;
            public final ColumnDefinition apiList;
            public final ColumnDefinition approved;
            public final ColumnDefinition approveMsg;
            public final ColumnDefinition dbsetId;
            public final ColumnDefinition fieldType;
        
        public TaskTableDefinition as(String alias) {
            return _as(alias);
        }
        public TaskTableDefinition inShard(String shardId) {
            return _inShard(shardId);
        }

        public TaskTableDefinition shardBy(String shardValue) {
            return _shardBy(shardValue);
        }

        public TaskTableDefinition() {
            super("task_table");
					id = column("id", JDBCType.BIGINT);
					projectId = column("project_id", JDBCType.INTEGER);
					tableNames = column("table_names", JDBCType.VARCHAR);
					viewNames = column("view_names", JDBCType.VARCHAR);
					customTableName = column("custom_table_name", JDBCType.VARCHAR);
					prefix = column("prefix", JDBCType.VARCHAR);
					suffix = column("suffix", JDBCType.VARCHAR);
					cudBySp = column("cud_by_sp", JDBCType.BIT);
					pagination = column("pagination", JDBCType.BIT);
					generated = column("generated", JDBCType.BIT);
					version = column("version", JDBCType.INTEGER);
					updateUserNo = column("update_user_no", JDBCType.VARCHAR);
					updateTime = column("update_time", JDBCType.TIMESTAMP);
					comment = column("comment", JDBCType.LONGVARCHAR);
					sqlStyle = column("sql_style", JDBCType.VARCHAR);
					apiList = column("api_list", JDBCType.LONGVARCHAR);
					approved = column("approved", JDBCType.INTEGER);
					approveMsg = column("approveMsg", JDBCType.LONGVARCHAR);
					dbsetId = column("dbset_id", JDBCType.INTEGER);
					fieldType = column("field_type", JDBCType.INTEGER);
		            setColumnDefinitions(
                        id, projectId, tableNames, viewNames, customTableName, prefix, suffix, cudBySp, 
                        pagination, generated, version, updateUserNo, updateTime, comment, sqlStyle, 
                        apiList, approved, approveMsg, dbsetId, fieldType
            );
        }
    }

	@NotNull(message = "{genTaskByTableViewSp.id.notNull}", groups = {UpdateTableEntity.class, DeleteTableEntity.class})
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@NotNull(message = "{genTaskByTableViewSp.project_id.notNull}", groups = {AddTableEntity.class, UpdateTableEntity.class})
	@Min(value = 0, message = "{genTaskByTableViewSp.project_id.notNull}", groups = {AddTableEntity.class, UpdateTableEntity.class})
	@Column(name = "project_id")
	private Long project_id;

	@NotBlank(message = "{genTaskByTableViewSp.table_names.notNull}", groups = {AddTableEntity.class, UpdateTableEntity.class})
	@Column(name = "table_names")
	private String table_names;

	@NotBlank(message = "{genTaskByTableViewSp.view_names.notNull}", groups = {AddTableEntity.class, UpdateTableEntity.class})
	@Column(name = "view_names")
	private String view_names;

	@Column(name = "custom_table_name")
	private String custom_table_name;

	@Column(name = "prefix")
	private String prefix;

	@Column(name = "suffix")
	private String suffix;

	@Column(name = "cud_by_sp")
	private Boolean cud_by_sp;

	@Column(name = "pagination")
	private Boolean pagination;

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

	@Column(name = "sql_style")
	private String sql_style;

	@Column(name = "api_list")
	private String api_list;

	@Column(name = "approved")
	private Integer approved;

	@Column(name = "approveMsg")
	private String approveMsg;

    /** task_table id **/
	@NotNull(message = "{genTaskByTableViewSp.dbset_id.notNull}", groups = {AddTableEntity.class, UpdateTableEntity.class})
	@Column(name = "dbset_id")
	private Integer dbset_id;

    /** 字段类型 **/
	@Column(name = "field_type")
	private Integer field_type;

	private String sp_names;

	private Integer alldbs_id;

	private String str_approved;

	private String str_update_time = "";

	private String dbsetName;

}