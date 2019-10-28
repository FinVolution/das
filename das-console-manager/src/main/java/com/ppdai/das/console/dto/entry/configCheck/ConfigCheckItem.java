package com.ppdai.das.console.dto.entry.configCheck;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfigCheckItem {

    //列标题
    private List<String> columnTitle;

    //关键字
    private List<TitleResponse> titles;

    private List<ConfigDasItem> list;

}
