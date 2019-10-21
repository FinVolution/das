package com.ppdai.platform.das.codegen.dto.entry.configCheck;

import lombok.Data;

@Data
public class TitleResponse {

    private String key;
    private String value;

    public TitleResponse(String key, String value) {
        this.key = key;
        this.value = value;
    }
}
