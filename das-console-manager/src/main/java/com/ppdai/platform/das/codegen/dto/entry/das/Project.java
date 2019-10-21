package com.ppdai.platform.das.codegen.dto.entry.das;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ppdai.das.client.ColumnDefinition;
import com.ppdai.das.client.TableDefinition;
import com.ppdai.platform.das.codegen.common.validates.group.project.AddProject;
import com.ppdai.platform.das.codegen.common.validates.group.project.DeleteProject;
import com.ppdai.platform.das.codegen.common.validates.group.project.UpdateProject;
import com.ppdai.platform.das.codegen.dto.model.Item;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.sql.JDBCType;
import java.util.Date;
import java.util.List;

/**
 * create by das-console
 * 请勿修改此文件
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table
public class Project {

    public static final ProjectDefinition PROJECT = new ProjectDefinition();

    public static class ProjectDefinition extends TableDefinition {
            public final ColumnDefinition id;
            public final ColumnDefinition name;
            public final ColumnDefinition namespace;
            public final ColumnDefinition dalGroupId;
            public final ColumnDefinition dalConfigName;
            public final ColumnDefinition updateUserNo;
            public final ColumnDefinition insertTime;
            public final ColumnDefinition updateTime;
            public final ColumnDefinition appId;
            public final ColumnDefinition appGroupId;
            public final ColumnDefinition preReleaseTime;
            public final ColumnDefinition appScene;
            public final ColumnDefinition comment;
            public final ColumnDefinition firstReleaseTime;
            public final ColumnDefinition token;
        
        public ProjectDefinition as(String alias) {
            return _as(alias);
        }
        public ProjectDefinition inShard(String shardId) {
            return _inShard(shardId);
        }

        public ProjectDefinition shardBy(String shardValue) {
            return _shardBy(shardValue);
        }

        public ProjectDefinition() {
            super("project");
					id = column("id", JDBCType.BIGINT);
					name = column("name", JDBCType.VARCHAR);
					namespace = column("namespace", JDBCType.VARCHAR);
					dalGroupId = column("dal_group_id", JDBCType.INTEGER);
					dalConfigName = column("dal_config_name", JDBCType.VARCHAR);
					updateUserNo = column("update_user_no", JDBCType.VARCHAR);
					insertTime = column("insert_time", JDBCType.TIMESTAMP);
					updateTime = column("update_time", JDBCType.TIMESTAMP);
					appId = column("app_id", JDBCType.VARCHAR);
					appGroupId = column("app_group_id", JDBCType.INTEGER);
					preReleaseTime = column("pre_release_time", JDBCType.TIMESTAMP);
					appScene = column("app_scene", JDBCType.LONGVARCHAR);
					comment = column("comment", JDBCType.LONGVARCHAR);
					firstReleaseTime = column("first_release_time", JDBCType.TIMESTAMP);
					token = column("token", JDBCType.VARCHAR);
		            setColumnDefinitions(
                        id, name, namespace, dalGroupId, dalConfigName, updateUserNo, insertTime, 
                        updateTime, appId, appGroupId, preReleaseTime, appScene, comment, 
                        firstReleaseTime, token
            );
        }
    }

	@NotNull(message = "{project.id.notNull}", groups = {UpdateProject.class, DeleteProject.class})
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@NotBlank(message = "{project.name.notNull}", groups = {AddProject.class, UpdateProject.class})
	@Column(name = "name")
	private String name;

	@NotBlank(message = "{project.namespace.notNull}", groups = {AddProject.class})
	@Column(name = "namespace")
	private String namespace;

	/**项目组ID**/
	@NotNull(message = "{project.dal_group_id.notNull}", groups = {AddProject.class})
	@Column(name = "dal_group_id")
	private Long dal_group_id;

	@Column(name = "dal_config_name")
	private String dal_config_name;

	/**操作人工号**/
	@Column(name = "update_user_no")
	private String update_user_no;

	@Column(name = "insert_time")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date create_time;

	@Column(name = "update_time")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
	private Date update_time;

	@NotNull(message = "{project.app_id.notNull}", groups = {AddProject.class, UpdateProject.class})
	@Column(name = "app_id")
	private String app_id;

	@Column(name = "app_group_id")
	private Long app_group_id;

    /** 预计上线时间 **/
	@Column(name = "pre_release_time")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
	private Date pre_release_time;

    /** 应用场景 **/
	@Column(name = "app_scene")
	private String app_scene;

	@Column(name = "comment")
	private String comment;

    /** 首次上线时间 **/
	@Column(name = "first_release_time")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
	private Date first_release_time;

    /** das token **/
	@Column(name = "token")
	private String token;

	private String groupName;

	private List<Item> items;

	private List<Item> users;

	private String dbsetNamees;

}