package com.ppdai.platform.das.codegen.dto.entry.das;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ppdai.das.client.ColumnDefinition;
import com.ppdai.das.client.TableDefinition;
import com.ppdai.platform.das.codegen.common.validates.group.group.AddDalGroup;
import com.ppdai.platform.das.codegen.common.validates.group.group.DeleteGroup;
import com.ppdai.platform.das.codegen.common.validates.group.group.UpdateDalGroup;
import com.ppdai.platform.das.codegen.dto.view.search.CheckTypes;
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
public class DasGroup {

    public static final DalGroupDefinition DASGROUP = new DalGroupDefinition();

    public static class DalGroupDefinition extends TableDefinition {
            public final ColumnDefinition id;
            public final ColumnDefinition groupName;
            public final ColumnDefinition groupComment;
            public final ColumnDefinition insertTime;
            public final ColumnDefinition updateTime;
            public final ColumnDefinition updateUserNo;
        
        public DalGroupDefinition as(String alias) {
            return _as(alias);
        }
        public DalGroupDefinition inShard(String shardId) {
            return _inShard(shardId);
        }

        public DalGroupDefinition shardBy(String shardValue) {
            return _shardBy(shardValue);
        }

        public DalGroupDefinition() {
            super("dal_group");
					id = column("id", JDBCType.BIGINT);
					groupName = column("group_name", JDBCType.VARCHAR);
					groupComment = column("group_comment", JDBCType.LONGVARCHAR);
					insertTime = column("insert_time", JDBCType.TIMESTAMP);
					updateTime = column("update_time", JDBCType.TIMESTAMP);
					updateUserNo = column("update_user_no", JDBCType.VARCHAR);
		            setColumnDefinitions(
                        id, groupName, groupComment, insertTime, updateTime, updateUserNo
            );
        }
    }

    @NotNull(message = "{dalGroup.group_name.notNull}", groups = {UpdateDalGroup.class, DeleteGroup.class})
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

    @NotBlank(message = "{dalGroup.group_name.notNull}", groups = {AddDalGroup.class, UpdateDalGroup.class})
	@Column(name = "group_name")
	private String group_name;

	@Column(name = "group_comment")
	private String group_comment;

	@Column(name = "insert_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date insert_time;

	@Column(name = "update_time")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date update_time;

    /** 最后操作人 **/
	@Column(name = "update_user_no")
	private String update_user_no;

    private String text;

    private String icon;

    private boolean children;

    private String userNo;

    private String userName;

    private String userRealName;

    private String userEmail;

    private CheckTypes roles;

}