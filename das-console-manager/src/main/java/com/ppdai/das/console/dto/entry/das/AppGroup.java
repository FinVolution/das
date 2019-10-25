package com.ppdai.das.console.dto.entry.das;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ppdai.das.client.ColumnDefinition;
import com.ppdai.das.client.TableDefinition;
import com.ppdai.das.console.common.validates.group.appGroup.AddAppGroup;
import com.ppdai.das.console.common.validates.group.appGroup.DeleteAppGroup;
import com.ppdai.das.console.common.validates.group.appGroup.UpdateAppGroup;
import com.ppdai.das.console.dto.model.Item;
import com.ppdai.das.console.dto.view.search.CheckTypes;
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
public class AppGroup {

    public static final AppGroupDefinition APPGROUP = new AppGroupDefinition();

    public static class AppGroupDefinition extends TableDefinition {
            public final ColumnDefinition id;
            public final ColumnDefinition name;
            public final ColumnDefinition serverGroupId;
            public final ColumnDefinition comment;
            public final ColumnDefinition insertTime;
            public final ColumnDefinition updateTime;
            public final ColumnDefinition updateUserNo;
            public final ColumnDefinition serverEnabled;
        
        public AppGroupDefinition as(String alias) {
            return _as(alias);
        }
        public AppGroupDefinition inShard(String shardId) {
            return _inShard(shardId);
        }

        public AppGroupDefinition shardBy(String shardValue) {
            return _shardBy(shardValue);
        }

        public AppGroupDefinition() {
            super("app_group");
					id = column("id", JDBCType.BIGINT);
					name = column("name", JDBCType.VARCHAR);
					serverGroupId = column("server_group_id", JDBCType.INTEGER);
					comment = column("comment", JDBCType.LONGVARCHAR);
					insertTime = column("insert_time", JDBCType.TIMESTAMP);
					updateTime = column("update_time", JDBCType.TIMESTAMP);
					updateUserNo = column("update_user_no", JDBCType.VARCHAR);
					serverEnabled = column("server_enabled", JDBCType.TINYINT);
		            setColumnDefinitions(
                        id, name, serverGroupId, comment, insertTime, updateTime, updateUserNo, 
                        serverEnabled
            );
        }
    }

	@NotNull(message = "{appGroup.id.notNull}", groups = {UpdateAppGroup.class, DeleteAppGroup.class})
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@NotBlank(message = "{appGroup.name.notNull}", groups = {AddAppGroup.class, UpdateAppGroup.class})
	@Column(name = "name")
	private String name;

	@Column(name = "server_group_id")
	private Long serverGroupId;

	@Column(name = "comment")
	private String comment;

	@Column(name = "insert_time")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date create_time;

	@Column(name = "update_time")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date update_time;

    /** 最后操作人 **/
	@Column(name = "update_user_no")
	private String update_user_no;

    /** 是否是远程连接Das Server的方式 0:否 1:是 **/
	@Column(name = "server_enabled")
	private Integer serverEnabled;

	private List<Item> items;

	private String serverGroupName;

	private CheckTypes serverEnableds;

	private String projectNames;
}