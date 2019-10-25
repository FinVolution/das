package com.ppdai.das.console.dto.entry.das;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ppdai.das.client.ColumnDefinition;
import com.ppdai.das.client.TableDefinition;
import com.ppdai.das.console.common.validates.group.member.AddMember;
import com.ppdai.das.console.common.validates.group.member.DeleteMember;
import com.ppdai.das.console.common.validates.group.member.UpdateMember;
import com.ppdai.das.console.dto.view.search.CheckTypes;
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
public class UserGroup {

    public static final UserGroupDefinition USERGROUP = new UserGroupDefinition();

    public static class UserGroupDefinition extends TableDefinition {
            public final ColumnDefinition id;
            public final ColumnDefinition userId;
            public final ColumnDefinition groupId;
            public final ColumnDefinition role;
            public final ColumnDefinition optUser;
            public final ColumnDefinition insertTime;
            public final ColumnDefinition updateTime;
            public final ColumnDefinition updateUserNo;
        
        public UserGroupDefinition as(String alias) {
            return _as(alias);
        }
        public UserGroupDefinition inShard(String shardId) {
            return _inShard(shardId);
        }

        public UserGroupDefinition shardBy(String shardValue) {
            return _shardBy(shardValue);
        }

        public UserGroupDefinition() {
            super("user_group");
					id = column("id", JDBCType.BIGINT);
					userId = column("user_id", JDBCType.INTEGER);
					groupId = column("group_id", JDBCType.INTEGER);
					role = column("role", JDBCType.TINYINT);
					optUser = column("opt_user", JDBCType.TINYINT);
					insertTime = column("insert_time", JDBCType.TIMESTAMP);
					updateTime = column("update_time", JDBCType.TIMESTAMP);
					updateUserNo = column("update_user_no", JDBCType.VARCHAR);
		            setColumnDefinitions(
                        id, userId, groupId, role, optUser, insertTime, updateTime, updateUserNo
            );
        }
    }

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@NotNull(message = "{userGroup.user_id.notNull}", groups = {AddMember.class, DeleteMember.class, UpdateMember.class})
	@Column(name = "user_id")
	private Long user_id;

	@NotNull(message = "{userGroup.group_id.notNull}", groups = {AddMember.class, DeleteMember.class, UpdateMember.class})
	@Column(name = "group_id")
	private Long group_id;

	@NotNull(message = "{userGroup.role.notNull}", groups = {AddMember.class, UpdateMember.class})
	@Column(name = "role")
	private Integer role;

	@NotNull(message = "{userGroup.opt_user.notNull}", groups = {AddMember.class, UpdateMember.class})
	@Column(name = "opt_user")
	private Integer opt_user;

	@Column(name = "insert_time")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date insert_time;

	@Column(name = "update_time")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date update_time;

    /** 最后操作人 **/
	@Column(name = "update_user_no")
	private String update_user_no;

	private String userNo;

	private String userName;

	private String userRealName;

	private String userEmail;

	private CheckTypes roles;

	private String group_name;

	private String group_comment;

}