package com.ppdai.platform.das.codegen.dto.entry.configCheck;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfigDataResponse {

    //数据源标题
    private String itemTitle;

    //项目关键字
    private List<TitleResponse> titles;

    //对比内容
    private List<ItemResponse> items;

}
