package com.ppdai.das.console.dto.entry.das;

import com.ppdai.das.client.ColumnDefinition;
import com.ppdai.das.client.TableDefinition;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.JDBCType;

/**
 * create by das-console
 * 请勿修改此文件
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table
public class UserProject {

    public static final UserProjectDefinition USERPROJECT = new UserProjectDefinition();

    public static class UserProjectDefinition extends TableDefinition {
            public final ColumnDefinition id;
            public final ColumnDefinition projectId;
            public final ColumnDefinition userId;
        
        public UserProjectDefinition as(String alias) {
            return _as(alias);
        }
        public UserProjectDefinition inShard(String shardId) {
            return _inShard(shardId);
        }

        public UserProjectDefinition shardBy(String shardValue) {
            return _shardBy(shardValue);
        }

        public UserProjectDefinition() {
            super("user_project");
					id = column("id", JDBCType.BIGINT);
					projectId = column("project_id", JDBCType.INTEGER);
					userId = column("user_id", JDBCType.INTEGER);
		            setColumnDefinitions(
                        id, projectId, userId
            );
        }
    }

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "project_id")
	private Long projectId;

	@Column(name = "user_id")
	private Long userId;

}