package com.ppdai.platform.das.console.dto.view.menu;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class MemuModel {
    private String header;
    private String className;
    private List<ItemModel> items;
}
