package com.ppdai.das.console.dto.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuItemModel {

    String menuName;    //显示名称

    String menuUrl;     //跳转地址
}
