package com.ppdai.das.console.dto.entry.das;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ppdai.das.client.ColumnDefinition;
import com.ppdai.das.client.TableDefinition;
import com.ppdai.das.console.common.validates.group.server.AddServerGroup;
import com.ppdai.das.console.common.validates.group.server.DeleteServerGroup;
import com.ppdai.das.console.common.validates.group.server.UpdateServerGroup;
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
public class ServerGroup {

    public static final ServerGroupDefinition SERVERGROUP = new ServerGroupDefinition();

    public static class ServerGroupDefinition extends TableDefinition {
            public final ColumnDefinition id;
            public final ColumnDefinition name;
            public final ColumnDefinition comment;
            public final ColumnDefinition insertTime;
            public final ColumnDefinition updateTime;
            public final ColumnDefinition updateUserNo;
        
        public ServerGroupDefinition as(String alias) {
            return _as(alias);
        }
        public ServerGroupDefinition inShard(String shardId) {
            return _inShard(shardId);
        }

        public ServerGroupDefinition shardBy(String shardValue) {
            return _shardBy(shardValue);
        }

        public ServerGroupDefinition() {
            super("server_group");
					id = column("id", JDBCType.BIGINT);
					name = column("name", JDBCType.VARCHAR);
					comment = column("comment", JDBCType.LONGVARCHAR);
					insertTime = column("insert_time", JDBCType.TIMESTAMP);
					updateTime = column("update_time", JDBCType.TIMESTAMP);
					updateUserNo = column("update_user_no", JDBCType.VARCHAR);
		            setColumnDefinitions(
                        id, name, comment, insertTime, updateTime, updateUserNo
            );
        }
    }

    @NotNull(message = "{serverGroup.name.notNull}", groups = {DeleteServerGroup.class})
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

    @NotBlank(message = "{serverGroup.name.notNull}", groups = {AddServerGroup.class, UpdateServerGroup.class})
	@Column(name = "name")
	private String name;

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

}