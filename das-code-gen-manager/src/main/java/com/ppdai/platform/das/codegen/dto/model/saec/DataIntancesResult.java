package com.ppdai.platform.das.codegen.dto.model.saec;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataIntancesResult {
    private String responseCode;
    private String responseMessage;
    private List<DataSaec> responseContent;
}
