package com.ppdai.platform.das.console.dto.view.treeSelect;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TreeSelectView {

    private String label;
    private String value;
    private String key;
    private List<TreeNodeView> children;

}
