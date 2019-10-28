package com.ppdai.das.console.dto.view.menu;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ItemModel {
    private String className;
    private String title;
    private String href;
    private List<ChildModel> children;
}
