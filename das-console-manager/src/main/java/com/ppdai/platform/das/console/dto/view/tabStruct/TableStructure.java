package com.ppdai.platform.das.console.dto.view.tabStruct;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TableStructure {

    private List<String> primaryKeys;               //主键
    private List<TableAttribute> tableAttributes;   //表属性
    private Integer total;                          //字段总数
}
