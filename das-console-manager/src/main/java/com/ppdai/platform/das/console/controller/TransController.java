package com.ppdai.platform.das.console.controller;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.googlejavaformat.java.Formatter;
import com.google.googlejavaformat.java.FormatterException;
import com.ppdai.platform.das.console.common.utils.StringUtil;
import com.ppdai.platform.das.console.config.annotation.CurrentUser;
import com.ppdai.platform.das.console.dto.entry.das.LoginUser;
import com.ppdai.platform.das.console.dto.model.ServiceResult;
import com.ppdai.platform.das.console.dto.model.TransRequest;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.builder.SqlSourceBuilder;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.scripting.defaults.RawSqlSource;
import org.apache.ibatis.scripting.xmltags.*;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeAliasRegistry;
import org.apache.ibatis.type.TypeException;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.newLinkedList;
import static com.google.common.collect.Maps.newLinkedHashMap;
import static java.lang.String.format;
import static org.apache.ibatis.mapping.SqlCommandType.*;

@Slf4j
@RestController
@RequestMapping(value = "/trans")
public class TransController {

    private final static String SEP = System.getProperty("line.separator");

    private final static Splitter DOT_SPLITTER = Splitter.on(".").trimResults();

    @RequestMapping(value = "/toDas", method = RequestMethod.POST)
    public ServiceResult<String> convert(@RequestBody TransRequest transRequest, @CurrentUser LoginUser user, Errors errors) {

        try {
            Configuration configuration = createConfiguration();

            InputStream inputStream = new ByteArrayInputStream(transRequest.getXmlContent().getBytes(StandardCharsets.UTF_8));
            XMLMapperBuilder mapperParser = new XMLMapperBuilder(inputStream, configuration, "any.xml", configuration.getSqlFragments());
            mapperParser.parse();

            Collection<MappedStatement> statements = uniqueStatements(mapperParser.getConfiguration().getMappedStatements());
            List<Statement> convertedStatements = newArrayList();
            VelocityContext context = new VelocityContext();

            Set<String> importResultMaps = importResultMaps(configuration);
            context.put("importResultMaps", importResultMaps);
            context.put("classComments", classComments(importResultMaps));
            context.put("className", createClassName(mapperParser));
            context.put("statements", convertedStatements);

            for(MappedStatement statement : statements) {
                if (statement.getSqlCommandType() == SELECT || statement.getSqlCommandType() == INSERT
                        || statement.getSqlCommandType() == UPDATE || statement.getSqlCommandType() == DELETE) {
                    try{
                        SqlSource sqlSource = statement.getSqlSource();
                        ArrayList<Seg> segs = new ArrayList<>();
                        LinkedHashMap<String, String> params = newLinkedHashMap();

                        Class parameterType = statement.getParameterMap().getType();
                        StringBuilder comments = new StringBuilder();
                        if(statement.isUseCache()) {
                            //comments.append(SEP).append("不支持useCache属性");
                        }
                        if(sqlSource instanceof RawSqlSource) {
                            List<ParameterMapping> parameterMappings = get(get(sqlSource, "sqlSource"), "parameterMappings");
                            LinkedHashMap<String, String> parsedParams = getParams(parameterMappings);

                            StaticTextSqlNode node = new StaticTextSqlNode(get(get(sqlSource, "sqlSource"), "sql"));
                            walkNodes(node, configuration, statement, segs, new AtomicInteger(0), params, newLinkedList(), parsedParams, parameterType, comments, Sets.newHashSet());
                        } else {
                            walkNodes(get(sqlSource, "rootSqlNode"), configuration, statement, segs, new AtomicInteger(0), params, newLinkedList(), newLinkedHashMap(), parameterType, comments, Sets.newHashSet());
                        }

                        String resultType = getResultType(statement).getSimpleName();

                        if(parameterType != null){
                            params.clear();
                            params.put("p" + parameterType.getSimpleName(),  parameterType.getName());
                        }

                        Class resultClass = getResultType(statement);
                        if(statement.getSqlCommandType() == SELECT ){
                            segs.add(new Seg("sqlBuilder." + getIntoType(resultClass) + ";"));
                        }
                        Statement converted = new Statement()
                                .setId(statement.getId())
                                .setMethodBody(join(segs))
                                .setMethodType(statement.getSqlCommandType() == SELECT ? "query" : "update")
                                .setResultType(statement.getSqlCommandType() == SELECT && !isPrimitiveType(resultClass) ? "List<" + resultType + ">" : resultType)
                                .setParams(params)
                                .setComments(comments.toString());

                        convertedStatements.add(converted);
                    }catch (Exception e){
                        Statement converted = new Statement()
                                .setId(statement.getId())
                                .setFail(true);
                        convertedStatements.add(converted);
                        e.printStackTrace();
                    }
                }
            }

            String finalResult = mergeTemplate(context);
            String formattedSource = finalResult;
            try {
                formattedSource = new Formatter().formatSource(finalResult);
            } catch (FormatterException fe) {
                fe.printStackTrace();
            }
            return ServiceResult.success(formattedSource);
        }catch (Exception e){
            e.printStackTrace();
            return ServiceResult.fail(StringUtil.getMessage(e));
        }
    }

    private Class getResultType(MappedStatement statement) {
        if(statement.getSqlCommandType() == SELECT) {
            if(statement.getResultMaps().isEmpty()) {
                return Void.class;
            } else {
                return statement.getResultMaps().get(0).getType();
            }
        }
        return int.class;//for update, insert, delete
    }

    private List<String> classComments(Set<String> importResultMaps) {
        return importResultMaps.stream().map(s -> SEP + "请用Das生成的实体类替代: " + s).collect(Collectors.toList());
    }

    private Set<String> importResultMaps(Configuration configuration) {
        return configuration.getResultMaps().stream().map(
                m -> m.getType().getName()
        ).collect(Collectors.toSet());
    }

    private String createClassName(XMLMapperBuilder xmlMapperBuilder) throws NoSuchFieldException, IllegalAccessException {
        MapperBuilderAssistant mapperBuilderAssistant = get(xmlMapperBuilder, "builderAssistant");
        return Iterables.getLast(DOT_SPLITTER.splitToList(mapperBuilderAssistant.getCurrentNamespace()));
    }

    private ImmutableMap<JdbcType, String> jdbcType2Of = ImmutableMap.<JdbcType, String>builder()
            .put(JdbcType.INTEGER, "integer")
            .put(JdbcType.VARCHAR, "varchar")
            .put(JdbcType.BOOLEAN, "boolean")
            .build();

    private LinkedHashMap<String, String> getParams(List<ParameterMapping> parameterMappings) {
        LinkedHashMap<String, String> parsedParams = newLinkedHashMap();
        for(ParameterMapping mapping : parameterMappings) {
            String type;
            if(mapping.getJavaType() != Object.class) {
                type = mapping.getJavaType().getSimpleName().toLowerCase();
            } else {
                if(mapping.getJdbcType() == null) {
                    type = "object";
                } else {
                    type = mapping.getJdbcType().name().toLowerCase();
                }
            }
            parsedParams.put(mapping.getProperty(), type);
        }
        return parsedParams;
    }

    private Collection<MappedStatement> uniqueStatements(Collection<MappedStatement> mappedStatements) {
        HashMap<String, MappedStatement> map = newLinkedHashMap();
        for(MappedStatement ms : mappedStatements) {
            map.put(ms.getId(), ms);
        }
        return map.values();
    }

    private void walkNodes(SqlNode node, Configuration  configuration, MappedStatement statement,
                            ArrayList<Seg> collector, AtomicInteger level, LinkedHashMap<String, String> params,
                            LinkedList<String> tests, LinkedHashMap<String, String> parsedParams, Class parameterType, StringBuilder comments, Set<String> binds) throws NoSuchFieldException, IllegalAccessException {
        if(node instanceof MixedSqlNode) {
            List<SqlNode> contents = get(node, "contents");
            for(SqlNode n: contents){
                walkNodes(n, configuration, statement, collector, level, params, tests, parsedParams, parameterType, comments, binds);
            }

        } else if(node instanceof StaticTextSqlNode || node instanceof TextSqlNode){
            SqlSourceBuilder sqlSourceParser = new SqlSourceBuilder(configuration);
            SqlSource sqlSource = sqlSourceParser.parse(get(node, "text"), Object.class, new HashMap<>());

            LinkedHashMap<String, String> segParam = parsedParams.isEmpty() ? getParams(sqlSource.getBoundSql(null).getParameterMappings()) : parsedParams;
            String sql = trimLine(get(sqlSource, "sql"));
            if(StringUtils.isBlank(sql)){
                return;
            }
            StringBuilder sb = new StringBuilder();
            if (sql.contains("${")){
                sb.append("//此SQL包含${}操作,请用户自行处理").append(SEP);
            }
            if(level.get() == 0){
                sb.append(format("sqlBuilder.appendTemplate(\"%s\"", trimLine(sql)));

            } else {
                sb.append(format("sqlBuilder.appendTemplateWhen((%s), \"%s\"", Joiner.on(" && ").join(tests), trimLine(sql)));
            }
            if(!segParam.isEmpty()){
                List<String> tmp = newArrayList();

                for(Map.Entry<String, String> en : segParam.entrySet()){
                    String pValue;
                    if( parameterType == Long.class  || parameterType == Integer.class || parameterType == String.class){
                        pValue = "p" + parameterType.getSimpleName();
                    } else if(parameterType == null) {
                        pValue = en.getKey();//implicit parameter
                    } else {
                        pValue = "p" + parameterType.getSimpleName() + ".get" + StringUtils.capitalize(en.getKey()) + "()";
                    }
                    tmp.add(format("Argument.%sOf(\"\", %s)",
                            en.getValue().toLowerCase().replaceAll("\\[", "").replaceAll("]", ""),
                            pValue));
                }
                sb.append(", ").append(Joiner.on(", ").join(tmp)).append(");");
            } else {
                sb.append(");");
            }
            collector.add(new Seg(sb.toString(), level.get(), segParam));
            params.putAll(segParam);

        } else if(node instanceof VarDeclSqlNode) {
            binds.add(get(node, "name"));
            collector.add(new Seg(format("BindObject %s = %s;", get(node, "name"), get(node, "expression")), level.get(),  newLinkedHashMap()));

        } else if(node instanceof IfSqlNode) {
            tests.addLast(get(node, "test"));
            level.incrementAndGet();
            walkNodes(get(node, "contents"), configuration, statement,collector, level, params, tests, parsedParams, parameterType, comments, binds);
            level.decrementAndGet();
            tests.pollLast();

        } else if(node instanceof ChooseSqlNode){
            List<SqlNode> ifs = get(node, "ifSqlNodes");
            for(SqlNode sqlNode : ifs){
                walkNodes(sqlNode,configuration, statement,collector, level, params, tests, parsedParams, parameterType, comments, binds);
            }
            SqlNode elseNode = get(node, "defaultSqlNode");
            if(elseNode != null) {
                level.incrementAndGet();
                tests.addLast("otherwise");
                walkNodes(elseNode, configuration, statement,collector, level, params, tests, parsedParams, parameterType, comments, binds);
                tests.pollLast();
                level.decrementAndGet();
            }

        } else if(node instanceof TrimSqlNode){//Include <where>,<set>
            StringBuilder sb = new StringBuilder()
                    .append("sqlBuilder.appendTemplate(\"").append(trimLine(get(node, "prefix"))).append("\");");
            collector.add(new Seg(sb.toString(), level.get()));

            int prefixesToOverridePosition = collector.size();
            walkNodes(get(node, "contents"), configuration, statement,collector, level, params, tests, parsedParams, parameterType, comments, binds);
            int suffixesToOverride = collector.size() -1;

            //applyPrefix(collector, prefixesToOverridePosition, get(node, "prefixesToOverride"));
            //applySuffix(collector, suffixesToOverride, get(node, "suffixesToOverride"));
            collector.add(new Seg(get(node, "suffix"), level.get()));
        } else if(node instanceof ForEachSqlNode){
            collector.add(new Seg(format("//请自行转换XML节点: <foreach item= \"%s\" index=\"%s\" collection=\"%s\" ...>",
                    get(node, "item"), get(node, "index"), get(node, "collectionExpression")), level.get()));
        }
    }

    private boolean isPrimitiveType(Class type) {
        return type == Integer.class || type == Long.class || type == int.class;
    }

    private String getIntoType(Class type) {
        if(isPrimitiveType(type)) {
            return "intoObject()";
        } else {
            return "into(" + type.getSimpleName()+ ".class)";
        }
    }

    private <T> T get(Object obj, String field) throws NoSuchFieldException, IllegalAccessException {
        return (T) getField(obj.getClass(), field).get(obj);
    }

    private Field getField(Class clz, String field) throws NoSuchFieldException {
        try {
            Field f = clz.getDeclaredField(field);
            f.setAccessible(true);
            return f;
        } catch (NoSuchFieldException e){
            if(clz != Object.class) {
                return getField(clz.getSuperclass(), field);
            } else {
                throw e;
            }
        }
    }

    private Configuration createConfiguration() throws NoSuchFieldException, IllegalAccessException {
        Configuration configuration = new Configuration();
        Field f = configuration.getClass().getDeclaredField("typeAliasRegistry");
        f.setAccessible(true);
        f.set(configuration, new DynamicTypeAliasRegistry());

        return configuration;
    }

    private String mergeTemplate(Context context) {
        java.util.Properties property = new java.util.Properties();
        property.setProperty(VelocityEngine.RUNTIME_LOG_LOGSYSTEM_CLASS,
                "org.apache.velocity.runtime.log.NullLogChute");
        property.setProperty(VelocityEngine.RESOURCE_LOADER, "class");
        property.setProperty("class.resource.loader.class",
                "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        Velocity.init(property);

        StringWriter writer = new StringWriter();
        Velocity.mergeTemplate("templates/convert/convert1.tpl", "UTF-8", context, writer);
        return  writer.toString();
    }

    private String join(ArrayList<Seg> segs){
        return Joiner.on(SEP).skipNulls().join(segs.stream().map(s->s.code).collect(Collectors.toList()));
    }

    public static class DynamicTypeAliasRegistry extends TypeAliasRegistry {
        @Override
        public <T> Class<T> resolveAlias(String string) {
            try {
                super.resolveAlias(string);
            }catch (TypeException e){
                ClassPool cp = ClassPool.getDefault();
                CtClass clz = cp.makeClass(string);
                try {
                    Class result = clz.toClass();
                } catch (CannotCompileException e1) {
                    e1.printStackTrace();
                }
            }
            return super.resolveAlias(string);
        }
    }

    private String trimLine(String s){
        String result = s.replaceAll("\n", "");
        return " " + result.trim() + " ";
    }

    static class Seg{
        String code;
        int level;
        String test;

        LinkedHashMap<String, String> params = newLinkedHashMap();

        Seg(String code ) {
            this.code = code;
        }

        Seg(String code, int level) {
            this(code);
            this.level = level;
        }

        Seg(String code, int level, LinkedHashMap<String, String> params) {
            this(code, level);
            this.params.putAll(params);
        }
    }

    static public class Statement {
        String id = "";
        String methodBody = "";
        String methodType = "";
        String resultType = "";
        String comments = "";
        boolean fail = false;
        LinkedHashMap<String, String> params = newLinkedHashMap();

        public Statement(){}

        public String getComments() {
            return comments;
        }

        public Statement setComments(String comments) {
            this.comments = comments;
            return this;
        }

        public String getResultType() {
            return resultType;
        }

        public Statement setResultType(String resultType) {
            this.resultType = resultType;
            return this;
        }

        public String getMethodType() {
            return methodType;
        }

        public Statement setMethodType(String methodType) {
            this.methodType = methodType;
            return this;
        }

        public String getIdAsName() {
            Iterable<String> it = DOT_SPLITTER.split(id);
            return Iterables.getLast(it);
        }

        public Statement setId(String id) {
            this.id = id;
            return this;
        }

        public String getMethodBody() {
            return methodBody;
        }

        public Statement setMethodBody(String methodBody) {
            this.methodBody = methodBody;
            return this;
        }

        public LinkedHashMap<String, String> getParams() {
            return params;
        }

        public Statement setParams(LinkedHashMap<String, String> params) {
            this.params = params;
            return this;
        }

        public boolean isFail() {
            return fail;
        }

        public Statement setFail(boolean fail) {
            this.fail = fail;
            return this;
        }
    }
}
