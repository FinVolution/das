package com.ppdai.platform.das.codegen.dto.entry.das;

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
public class DataSearchLog {

    public static final DataSearchLogDefinition DATASEARCHLOG = new DataSearchLogDefinition();

    public static class DataSearchLogDefinition extends TableDefinition {
            public final ColumnDefinition id;
            public final ColumnDefinition ip;
            public final ColumnDefinition requestType;
            public final ColumnDefinition request;
            public final ColumnDefinition success;
            public final ColumnDefinition result;
            public final ColumnDefinition userNo;
            public final ColumnDefinition inserttime;
            public final ColumnDefinition updatetime;
            public final ColumnDefinition isactive;
        
        public DataSearchLogDefinition as(String alias) {
            return _as(alias);
        }
        public DataSearchLogDefinition inShard(String shardId) {
            return _inShard(shardId);
        }

        public DataSearchLogDefinition shardBy(String shardValue) {
            return _shardBy(shardValue);
        }

        public DataSearchLogDefinition() {
            super("data_search_log");
					id = column("id", JDBCType.BIGINT);
					ip = column("ip", JDBCType.VARCHAR);
					requestType = column("request_type", JDBCType.TINYINT);
					request = column("request", JDBCType.LONGVARCHAR);
					success = column("success", JDBCType.BIT);
					result = column("result", JDBCType.LONGVARCHAR);
					userNo = column("user_no", JDBCType.VARCHAR);
					inserttime = column("inserttime", JDBCType.TIMESTAMP);
					updatetime = column("updatetime", JDBCType.TIMESTAMP);
					isactive = column("isactive", JDBCType.BIT);
		            setColumnDefinitions(
                        id, ip, requestType, request, success, result, userNo, inserttime, updatetime, 
                        isactive
            );
        }
    }


    /** 自增主键 **/
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "ip")
	private String ip;

    /** 类型：1、查询 0、下载 **/
	@Column(name = "request_type")
	private Integer request_type;

    /** 请求参数 **/
	@Column(name = "request")
	private String request;

    /** 请求：1、成功 0、失败 **/
	@Column(name = "success")
	private Boolean success;

    /** 异常信息等记录 **/
	@Column(name = "result")
	private String result;

    /** 操作人工号 **/
	@Column(name = "user_no")
	private String user_no;

    /** 插入时间 **/
	@Column(name = "inserttime")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date insert_time;

    /** 更新时间 **/
	@Column(name = "updatetime")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date update_time;

    /** 逻辑删除 **/
	@Column(name = "isactive")
	private Boolean isactive;

    private String user_real_name;
}