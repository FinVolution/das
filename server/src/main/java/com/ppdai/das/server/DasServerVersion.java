package com.ppdai.das.server;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DasServerVersion {

    private static final String version;

    static {
        version = initVersion();
    }

    private static String initVersion() {
        String version = "UNKNOWN";
        String path = "/server-version.prop";
        InputStream stream = DasServerVersion.class.getResourceAsStream(path);
        if (stream != null) {
            Properties props = new Properties();
            try {
                props.load(stream);
                stream.close();
                version = (String) props.get("version");
            } catch (IOException ignored) {
            }
        }
        return version;
    }

    public static String getVersion() {
        return version;
    }
    
    public static String getLoggerName() {
        return "DAS Server" + version;
    }
}
