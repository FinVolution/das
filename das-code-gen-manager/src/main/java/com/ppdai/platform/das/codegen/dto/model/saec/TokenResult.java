package com.ppdai.platform.das.codegen.dto.model.saec;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenResult {
    private String responseCode;
    private String responseMessage;
    private String responseContent;
}
