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
public class ServerConfig {

    public static final ServerConfigDefinition SERVERCONFIG = new ServerConfigDefinition();

    public static class ServerConfigDefinition extends TableDefinition {
            public final ColumnDefinition id;
            public final ColumnDefinition serverId;
            public final ColumnDefinition keya;
            public final ColumnDefinition value;
            public final ColumnDefinition comment;
            public final ColumnDefinition insertTime;
            public final ColumnDefinition updateTime;
            public final ColumnDefinition updateUserNo;
        
        public ServerConfigDefinition as(String alias) {
            return _as(alias);
        }
        public ServerConfigDefinition inShard(String shardId) {
            return _inShard(shardId);
        }

        public ServerConfigDefinition shardBy(String shardValue) {
            return _shardBy(shardValue);
        }

        public ServerConfigDefinition() {
            super("server_config");
					id = column("id", JDBCType.BIGINT);
					serverId = column("server_id", JDBCType.INTEGER);
					keya = column("keya", JDBCType.VARCHAR);
					value = column("value", JDBCType.VARCHAR);
					comment = column("comment", JDBCType.LONGVARCHAR);
					insertTime = column("insert_time", JDBCType.TIMESTAMP);
					updateTime = column("update_time", JDBCType.TIMESTAMP);
					updateUserNo = column("update_user_no", JDBCType.VARCHAR);
		            setColumnDefinitions(
                        id, serverId, keya, value, comment, insertTime, updateTime, updateUserNo
            );
        }
    }


	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

    /** server.id **/
	@Column(name = "server_id")
	private Integer serverId;

	@Column(name = "keya")
	private String keya;

	@Column(name = "value")
	private String value;

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