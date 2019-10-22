package com.ppdai.platform.das.console.dto.view;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SqlValidateView {

    private Integer dbType;
    private Integer rows;
    private String msg;

}
