package com.ppdai.platform.das.codegen.common.codeGen;

import com.ppdai.platform.das.codegen.common.codeGen.generator.java.context.JavaCodeGenContext;

public interface DalProcessor {
    void process(JavaCodeGenContext context) throws Exception;
}
