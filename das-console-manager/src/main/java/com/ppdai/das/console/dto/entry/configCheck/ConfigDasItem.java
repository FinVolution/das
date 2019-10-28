package com.ppdai.das.console.dto.entry.configCheck;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfigDasItem {

    private ItemResponse config;
    private ItemResponse das;

}
