package com.ppdai.platform.das.codegen.common.utils;

import com.google.common.collect.Sets;
import com.ppdai.platform.das.codegen.dto.model.Paging;
import com.ppdai.platform.das.codegen.dto.view.search.CheckTypes;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
public class SelectCoditonBuilder {
    public final String WHERE = " WHERE";
    private Set<String> set = Sets.newConcurrentHashSet();
    private CopyOnWriteArrayList<Codition> expressions = new CopyOnWriteArrayList<>();
    private String TAB = StringUtils.EMPTY;

    public SelectCoditonBuilder setTab(String tab) {
        this.TAB = tab;
        return this;
    }

    public static SelectCoditonBuilder getInstance() {
        return new SelectCoditonBuilder();
    }

    private SelectCoditonBuilder append(String key, Object val) {
        expressions.add(new Codition(key, val));
        return this;
    }

    private SelectCoditonBuilder and() {
        return append("and", StringUtils.EMPTY);
    }

    public SelectCoditonBuilder equal(boolean bool, String key, Object val) {
        if (bool) {
            return equal(key, val);
        }
        return this;
    }

    public SelectCoditonBuilder equal(String key, Object val) {
        if (null != val) {
            if (StringUtils.isNotBlank(key)) {
                if (val instanceof String) {
                    if (StringUtils.isNotBlank((String) val)) {
                        String _val = val.toString().trim();
                        and().append(TAB + key, "='" + val + "'");
                    }
                } else if (val instanceof Integer || val instanceof Long || val instanceof Double || val instanceof Float) {
                    and().append(TAB + key, "=" + val);
                }
            }
        }
        return this;
    }

    public SelectCoditonBuilder unEqual(String key, Object val) {
        if (null != val) {
            if (StringUtils.isNotBlank(key)) {
                if (val instanceof String && StringUtils.isNotBlank((String) val)) {
                    String _val = val.toString().trim();
                    and().append(TAB + key, "!='" + _val + "'");
                } else {
                    and().append(TAB + key, "!=" + val);
                }
            }
        }
        return this;
    }

    public SelectCoditonBuilder likeLeft(String key, String val) {
        if (StringUtils.isNotBlank(key) && StringUtils.isNotBlank(val)) {
            val = val.trim();
            and().append(TAB + key, "like '" + val + "%'");
        }
        return this;
    }

    public SelectCoditonBuilder likeRight(String key, String val) {
        if (StringUtils.isNotBlank(key) && StringUtils.isNotBlank(val)) {
            val = val.trim();
            and().append(TAB + key, "like '%" + val + "'");
        }
        return this;
    }

    public SelectCoditonBuilder like(String key, String val) {
        if (StringUtils.isNotBlank(key) && StringUtils.isNotBlank(val)) {
            val = val.trim();
            and().append(TAB + key, "like '%" + val + "%'");
        }
        return this;
    }

    public SelectCoditonBuilder in(String key, CheckTypes checkTypes, Class type) {
        if (StringUtils.isNotBlank(key) && checkTypes != null) {
            boolean checkAll = checkTypes.isCheckAll();
            List keys = checkTypes.getCheckeds();
            if (checkAll) {
                return this;
            } else {
                if (CollectionUtils.isNotEmpty(keys)) {
                    StringBuffer sql = new StringBuffer();
                    if (type.getClass().equals(String.class)) {
                        for (int i = 0; i < keys.size(); i++) {
                            String str = String.valueOf(keys.get(i));
                            if (i < keys.size() - 1) {
                                sql.append("'" + str + "',");
                            } else {
                                sql.append("'" + str + "'");
                            }
                        }
                    } else {
                        for (int i = 0; i < keys.size(); i++) {
                            String str = String.valueOf(keys.get(i));
                            if (i < keys.size() - 1) {
                                sql.append(str + ",");
                            } else {
                                sql.append(str);
                            }
                        }
                    }
                    and().append(TAB + key, "in (" + sql.toString() + ")");
                } else {
                    and().append(TAB + key, " = -999");
                }
            }
        }
        return this;
    }

    public SelectCoditonBuilder rangeData(String key, List<String> rangeData) {
        if (StringUtils.isNotBlank(key) && CollectionUtils.isNotEmpty(rangeData)) {
            and().append(TAB + key, " >= '" + rangeData.get(0) + "'");
            and().append(TAB + key, " <= '" + rangeData.get(1) + "'");
        }
        return this;
    }

    public SelectCoditonBuilder orderBy(Paging paging) {
        if (StringUtils.isNotBlank(paging.getSort())) {
            append("order by " + TAB + paging.getSort(), paging.isAscending() ? "ASC" : "DESC");
        }
        return this;
    }

    public SelectCoditonBuilder groupBy(String groupBy) {
        return append("group by ", groupBy);
    }

    public SelectCoditonBuilder limit(Paging paging) {
        return append("limit " + String.valueOf(paging.getOffset()), ", " + paging.getPageSize());
    }

    public SelectCoditonBuilder where() {
        if (set.contains(WHERE.trim())) {
            return this;
        }
        set.add(WHERE.trim());
        return append(WHERE, " 1=1 ");
    }

    public String builer() {
        StringBuffer sql = new StringBuffer();
        for (Codition codition : expressions) {
            sql.append(" ").append(codition.getKey()).append(" ").append(codition.getVal());
        }
        this.clear();
        String _sql = sql.toString().replaceAll(" ", " ");
        return _sql;
    }

    private void clear() {
        TAB = StringUtils.EMPTY;
        set = new HashSet<>();
        expressions = new CopyOnWriteArrayList<>();
    }

    class Codition {
        private String key;
        private Object val;

        public Codition(String key, Object val) {
            this.key = key;
            this.val = val;
        }

        public String getKey() {
            return key;
        }

        public Object getVal() {
            return val;
        }

    }
}
