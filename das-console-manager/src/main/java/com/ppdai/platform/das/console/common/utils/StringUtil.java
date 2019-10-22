package com.ppdai.platform.das.console.common.utils;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.collections.SetUtils;
import org.apache.commons.lang.StringUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {

    /**
     * tom(1) --> 1
     *
     * @param str
     * @return
     */
    public static String getNumber(String str) {
        Pattern pattern = Pattern.compile(".+\\((\\w+)\\).*");
        Matcher m = pattern.matcher(str);
        if (m.find()) {
            return m.group(1);
        }
        return str;
    }


    public static <T> String joinCollectByComma(Collection<T> collection) {
        return Joiner.on(",").skipNulls().join(collection);
    }

    public static Set<String> toSet(String values) {
        if (StringUtils.isNotBlank(values)) {
            List<String> list = toList(values, ",");
            return new HashSet(list);
        }
        return SetUtils.EMPTY_SET;
    }

    /**
     * "a,b,c,d ---> [a,b,c,d]"
     */
    public static List<String> toList(String values) {
        if (StringUtils.isNotBlank(values)) {
            return toList(values, ",");
        }
        return ListUtils.EMPTY_LIST;
    }


    public static List<String> toList(String values, String separator) {
        if (StringUtils.isNotBlank(values)) {
            return Splitter.on(separator).omitEmptyStrings().trimResults().splitToList(values);
        }
        return ListUtils.EMPTY_LIST;
    }

    public static String getMessage(Exception e) {
        if (StringUtils.isNotBlank(e.getMessage())) {
            return e.getMessage();
        }
        return e.toString();
    }
}
