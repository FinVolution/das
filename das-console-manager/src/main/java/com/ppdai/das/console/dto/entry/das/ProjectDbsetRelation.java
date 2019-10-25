package com.ppdai.das.console.dto.entry.das;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ppdai.das.client.ColumnDefinition;
import com.ppdai.das.client.TableDefinition;
import com.ppdai.das.console.common.validates.group.projectDbsetRelation.AddProjectDbsetRelation;
import com.ppdai.das.console.common.validates.group.projectDbsetRelation.DeleteProjectDbsetRelation;
import com.ppdai.das.console.common.validates.group.projectDbsetRelation.UpdateProjectDbsetRelation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
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
public class ProjectDbsetRelation {

    public static final ProjectDbsetRelationDefinition PROJECTDBSETRELATION = new ProjectDbsetRelationDefinition();

    public static class ProjectDbsetRelationDefinition extends TableDefinition {
            public final ColumnDefinition id;
            public final ColumnDefinition dbsetId;
            public final ColumnDefinition projectId;
            public final ColumnDefinition updateUserNo;
            public final ColumnDefinition insertTime;
            public final ColumnDefinition updateTime;
        
        public ProjectDbsetRelationDefinition as(String alias) {
            return _as(alias);
        }
        public ProjectDbsetRelationDefinition inShard(String shardId) {
            return _inShard(shardId);
        }

        public ProjectDbsetRelationDefinition shardBy(String shardValue) {
            return _shardBy(shardValue);
        }

        public ProjectDbsetRelationDefinition() {
            super("project_dbset_relation");
					id = column("id", JDBCType.BIGINT);
					dbsetId = column("dbset_id", JDBCType.INTEGER);
					projectId = column("project_id", JDBCType.INTEGER);
					updateUserNo = column("update_user_no", JDBCType.VARCHAR);
					insertTime = column("insert_time", JDBCType.TIMESTAMP);
					updateTime = column("update_time", JDBCType.TIMESTAMP);
		            setColumnDefinitions(
                        id, dbsetId, projectId, updateUserNo, insertTime, updateTime
            );
        }
    }

	@NotNull(message = "{projectDbsetRelation.id.notNull}", groups = {UpdateProjectDbsetRelation.class, DeleteProjectDbsetRelation.class})
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@NotNull(message = "{projectDbsetRelation.dbset_id.notNull}", groups = {AddProjectDbsetRelation.class, UpdateProjectDbsetRelation.class})
	@Column(name = "dbset_id")
	private Long dbsetId;

	@NotNull(message = "{projectDbsetRelation.project_id.notNull}", groups = {AddProjectDbsetRelation.class, UpdateProjectDbsetRelation.class})
	@Column(name = "project_id")
	private Long projectId;

	@Column(name = "update_user_no")
	private String updateUserNo;

	@Column(name = "insert_time")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date create_time;

	@Column(name = "update_time")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date update_time;

}