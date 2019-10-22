package com.ppdai.platform.das.console.dto.entry.configCheck;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfigSaecDasItem {

    private ItemResponse apollo;
    private ItemResponse das;
    private ItemResponse saec;

}
