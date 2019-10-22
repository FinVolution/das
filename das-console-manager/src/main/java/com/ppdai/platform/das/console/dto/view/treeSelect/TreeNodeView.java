package com.ppdai.platform.das.console.dto.view.treeSelect;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TreeNodeView {

    private String label;
    private String value;
    private String key;
}
