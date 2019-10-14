package com.ppdai.das.client.sqlbuilder;

import com.ppdai.das.client.Parameter;
import com.ppdai.das.client.ParameterDefinition;

public class Page extends Template {
    public static final String EMPTY = "PAGE_TEMPLATE_NOT_SPECIFIED? ?";
    public Page(int pageNo, int pageSize) {
        super(EMPTY, Parameter.integerOf("", (pageNo - 1) * pageSize), Parameter.integerOf("", pageSize));
    }

    public Page(String template, ParameterDefinition pageNo, ParameterDefinition pageSize) {
        super(EMPTY, pageNo, pageSize);
    }

    @Override
    public String build(BuilderContext context) {
        return context.getPageTemplate();
    }
}
