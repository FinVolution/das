package com.ppdai.platform.das.codegen.dto.entry.configCheck;

import com.ppdai.platform.das.codegen.enums.ConfigCheckTypeEnums;
import lombok.Data;

@Data
public class ItemResponse {

    private String key;
    private String value;
    private String type = ConfigCheckTypeEnums.SUCCESS.getValue();

    public ItemResponse(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public ItemResponse(String key, String value, String type) {
        this.key = key;
        this.value = value;
        this.type = type;
    }
}
