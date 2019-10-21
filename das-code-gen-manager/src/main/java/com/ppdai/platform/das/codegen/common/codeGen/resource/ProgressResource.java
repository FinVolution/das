package com.ppdai.platform.das.codegen.common.codeGen.resource;

import com.ppdai.platform.das.codegen.dto.entry.codeGen.Progress;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ProgressResource {
    /**
     * key: userNo + # + project_id + # + random value:Progress
     */
    public static Map<String, Progress> progresses = new ConcurrentHashMap<>();

    public final static String FINISH = "finish";
    public final static String ISDOING = "isDoing";

    public final static String INIT_MESSAGE = "正在初始化...";

    public static synchronized Progress getProgress(String userNo, Long project_id, String random) {
        String key = getKey(userNo, project_id, random);
        Progress pro = progresses.get(key);
        if (pro == null) {
            pro = new Progress();
            pro.setUserNo(userNo);
            pro.setProject_id(project_id);
            pro.setOtherMessage(INIT_MESSAGE);
            pro.setRandom(random);
            progresses.put(key, pro);
        }
        return pro;
    }

    private static String getKey(String userNo, Long project_id, String random) {
        return userNo + "#" + project_id + "#" + random;
    }

}
