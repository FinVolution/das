package com.ppdai.platform.das.console.dto.entry.das;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ppdai.das.client.ColumnDefinition;
import com.ppdai.das.client.TableDefinition;
import com.ppdai.platform.das.console.common.validates.group.server.AddServer;
import com.ppdai.platform.das.console.common.validates.group.server.DeleteServer;
import com.ppdai.platform.das.console.common.validates.group.server.UpdateServer;
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
public class Server {

    public static final ServerDefinition SERVER = new ServerDefinition();

    public static class ServerDefinition extends TableDefinition {
            public final ColumnDefinition id;
            public final ColumnDefinition serverGroupId;
            public final ColumnDefinition ip;
            public final ColumnDefinition port;
            public final ColumnDefinition comment;
            public final ColumnDefinition insertTime;
            public final ColumnDefinition updateTime;
            public final ColumnDefinition updateUserNo;
        
        public ServerDefinition as(String alias) {
            return _as(alias);
        }
        public ServerDefinition inShard(String shardId) {
            return _inShard(shardId);
        }

        public ServerDefinition shardBy(String shardValue) {
            return _shardBy(shardValue);
        }

        public ServerDefinition() {
            super("server");
					id = column("id", JDBCType.BIGINT);
					serverGroupId = column("server_group_id", JDBCType.INTEGER);
					ip = column("ip", JDBCType.VARCHAR);
					port = column("port", JDBCType.INTEGER);
					comment = column("comment", JDBCType.LONGVARCHAR);
					insertTime = column("insert_time", JDBCType.TIMESTAMP);
					updateTime = column("update_time", JDBCType.TIMESTAMP);
					updateUserNo = column("update_user_no", JDBCType.VARCHAR);
		            setColumnDefinitions(
                        id, serverGroupId, ip, port, comment, insertTime, updateTime, updateUserNo
            );
        }
    }

	@NotNull(message = "{server.id.notNull}", groups = {UpdateServer.class, DeleteServer.class})
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

    /** server_group.id **/
	@Column(name = "server_group_id")
	private Long serverGroupId;

	@NotBlank(message = "{server.ip.notNull}", groups = {AddServer.class, UpdateServer.class})
	@Column(name = "ip")
	private String ip;

	@NotNull(message = "{server.port.notNull}", groups = {AddServer.class, UpdateServer.class})
	@Column(name = "port")
	private Integer port;

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