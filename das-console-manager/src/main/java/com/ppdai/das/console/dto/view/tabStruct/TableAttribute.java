package com.ppdai.das.console.dto.view.tabStruct;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TableAttribute {

    private String columnName;
    private String columnType;
    private int datasize;
    private int digits;
    private int nullable;
}
