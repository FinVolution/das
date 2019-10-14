package com.ppdai.das.client;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DasClientVersion {
    private static final String version;

    static {
        version = initVersion();
    }

    private static String initVersion() {
        String path = "/version.prop";
        InputStream stream = DasClientVersion.class.getResourceAsStream(path);
        if (stream == null) {
            return "UNKNOWN";
        }
        Properties props = new Properties();
        try {
            props.load(stream);
            stream.close();
            return (String) props.get("version");
        } catch (IOException e) {
            return "UNKNOWN";
        }
    }

    public static String getVersion() {
        return version;
    }

    public static String getLoggerName() {
        return "DAS Client " + version;
    }

}
