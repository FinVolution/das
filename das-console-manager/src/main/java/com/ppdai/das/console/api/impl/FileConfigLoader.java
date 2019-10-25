package com.ppdai.das.console.api.impl;

import com.ppdai.das.console.api.ConfigLoader;

public class FileConfigLoader implements ConfigLoader {

    @Override
    public boolean isLoaderFile() {
        return true;
    }

}
