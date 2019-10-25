package com.ppdai.das.console.openapi.vo;

import com.ppdai.das.console.enums.DbMasterSlaveEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DatabaseSetEntryVO {

    /**
     * 逻辑库名
     */
    private String databaseSetName;

    /**
     * 逻辑库映射名
     */
    private String databasesetEntryName;

    /**
     * 物理库标识符
     */
    private String databaseName;

    /**
     * 主从类型 1.Master 2.Slave
     */
    private DbMasterSlaveEnum databaseMasterSlaveType;

    /**
     * sharding
     */
    private String sharding;

}
