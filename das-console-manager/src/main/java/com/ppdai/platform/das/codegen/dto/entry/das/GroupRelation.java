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
public class GroupRelation {

    public static final GroupRelationDefinition GROUPRELATION = new GroupRelationDefinition();

    public static class GroupRelationDefinition extends TableDefinition {
            public final ColumnDefinition id;
            public final ColumnDefinition currentGroupId;
            public final ColumnDefinition childGroupId;
            public final ColumnDefinition childGroupRole;
            public final ColumnDefinition adduser;
            public final ColumnDefinition updateUserNo;
            public final ColumnDefinition insertTime;
            public final ColumnDefinition updateTime;
        
        public GroupRelationDefinition as(String alias) {
            return _as(alias);
        }
        public GroupRelationDefinition inShard(String shardId) {
            return _inShard(shardId);
        }

        public GroupRelationDefinition shardBy(String shardValue) {
            return _shardBy(shardValue);
        }

        public GroupRelationDefinition() {
            super("group_relation");
					id = column("id", JDBCType.BIGINT);
					currentGroupId = column("current_group_id", JDBCType.INTEGER);
					childGroupId = column("child_group_id", JDBCType.INTEGER);
					childGroupRole = column("child_group_role", JDBCType.INTEGER);
					adduser = column("adduser", JDBCType.INTEGER);
					updateUserNo = column("update_user_no", JDBCType.VARCHAR);
					insertTime = column("insert_time", JDBCType.TIMESTAMP);
					updateTime = column("update_time", JDBCType.TIMESTAMP);
		            setColumnDefinitions(
                        id, currentGroupId, childGroupId, childGroupRole, adduser, updateUserNo, 
                        insertTime, updateTime
            );
        }
    }


	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "current_group_id")
	private Integer currentGroupId;

	@Column(name = "child_group_id")
	private Integer childGroupId;

	@Column(name = "child_group_role")
	private Integer childGroupRole;

	@Column(name = "adduser")
	private Integer adduser;

	@Column(name = "update_user_no")
	private String updateUserNo;

	@Column(name = "insert_time")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date insertTime;

	@Column(name = "update_time")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date updateTime;

}