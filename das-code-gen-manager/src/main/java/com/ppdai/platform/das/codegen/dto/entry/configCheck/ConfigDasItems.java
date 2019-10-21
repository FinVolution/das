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
public class ConfigDasItems {

    private List<ItemResponse> apollo;
    private List<ItemResponse> das;
    private String msg;
}
