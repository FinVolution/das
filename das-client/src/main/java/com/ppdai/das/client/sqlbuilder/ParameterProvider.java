package com.ppdai.das.client.sqlbuilder;

import java.util.List;

import com.ppdai.das.client.Parameter;

public interface ParameterProvider {
    /**
     * @return parameters embedded
     */
    List<Parameter> buildParameters();
}
