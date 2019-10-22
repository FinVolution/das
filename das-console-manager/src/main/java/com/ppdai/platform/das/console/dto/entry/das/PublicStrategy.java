package com.ppdai.platform.das.console.dto.entry.das;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ppdai.das.client.ColumnDefinition;
import com.ppdai.das.client.TableDefinition;
import com.ppdai.platform.das.console.common.validates.group.publicStrategy.AddPublicStrategy;
import com.ppdai.platform.das.console.common.validates.group.publicStrategy.DeletePublicStrategy;
import com.ppdai.platform.das.console.common.validates.group.publicStrategy.UpdatePublicStrategy;
import com.ppdai.platform.das.console.dto.view.search.CheckTypes;
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
public class PublicStrategy {

    public static final PublicStrategyDefinition PUBLICSTRATEGY = new PublicStrategyDefinition();

    public static class PublicStrategyDefinition extends TableDefinition {
            public final ColumnDefinition id;
            public final ColumnDefinition strategyLoadingType;
            public final ColumnDefinition className;
            public final ColumnDefinition strategySource;
            public final ColumnDefinition insertTime;
            public final ColumnDefinition updateTime;
            public final ColumnDefinition name;
            public final ColumnDefinition comment;
            public final ColumnDefinition updateUserNo;
            public final ColumnDefinition strategyParams;
        
        public PublicStrategyDefinition as(String alias) {
            return _as(alias);
        }
        public PublicStrategyDefinition inShard(String shardId) {
            return _inShard(shardId);
        }

        public PublicStrategyDefinition shardBy(String shardValue) {
            return _shardBy(shardValue);
        }

        public PublicStrategyDefinition() {
            super("public_strategy");
					id = column("id", JDBCType.BIGINT);
					strategyLoadingType = column("strategy_loading_type", JDBCType.TINYINT);
					className = column("class_name", JDBCType.VARCHAR);
					strategySource = column("strategy_source", JDBCType.LONGVARCHAR);
					insertTime = column("insert_time", JDBCType.TIMESTAMP);
					updateTime = column("update_time", JDBCType.TIMESTAMP);
					name = column("name", JDBCType.VARCHAR);
					comment = column("comment", JDBCType.LONGVARCHAR);
					updateUserNo = column("update_user_no", JDBCType.VARCHAR);
					strategyParams = column("strategy_params", JDBCType.LONGVARCHAR);
		            setColumnDefinitions(
                        id, strategyLoadingType, className, strategySource, insertTime, updateTime, 
                        name, comment, updateUserNo, strategyParams
            );
        }
    }

	@NotNull(message = "{publicStrategy.id.notNull}", groups = {UpdatePublicStrategy.class, DeletePublicStrategy.class})
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

    /** 策略类型：1、静态加载的策略 2、动态加载策略 **/
	@NotNull(message = "{publicStrategy.strategyLoadingType.notNull}", groups = {UpdatePublicStrategy.class, DeletePublicStrategy.class})
	@Column(name = "strategy_loading_type")
	private Integer strategyLoadingType;

	@NotBlank(message = "{publicStrategy.class_name.notNull}", groups = {AddPublicStrategy.class, UpdatePublicStrategy.class})
	@Column(name = "class_name")
	private String className;

	@Column(name = "strategy_source")
	private String strategySource;

	@NotBlank(message = "{publicStrategy.name.notNull}", groups = {AddPublicStrategy.class, UpdatePublicStrategy.class})
	@Column(name = "name")
	private String name;

	@Column(name = "comment")
	private String comment;

	@Column(name = "update_user_no")
	private String update_user_no;

    /** 策略参数 **/
	@Column(name = "strategy_params")
	private String strategyParams;

	@Column(name = "insert_time")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date insertTime;

	@Column(name = "update_time")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date updateTime;

	private CheckTypes strategyLoadingTypes;

}