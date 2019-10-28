package com.ppdai.das.console.dto.view.menu;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ChildModel {
    private String title;
    private String href;
    private String className;
}
