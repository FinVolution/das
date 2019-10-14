package com.ppdai.das.client;

import static com.ppdai.das.client.SegmentConstants.AND;
import static com.ppdai.das.client.SegmentConstants.COMMA;
import static com.ppdai.das.client.SegmentConstants.CROSS_JOIN;
import static com.ppdai.das.client.SegmentConstants.DELETE;
import static com.ppdai.das.client.SegmentConstants.DISTINCT;
import static com.ppdai.das.client.SegmentConstants.FROM;
import static com.ppdai.das.client.SegmentConstants.FULL_JOIN;
import static com.ppdai.das.client.SegmentConstants.GROUP_BY;
import static com.ppdai.das.client.SegmentConstants.HAVING;
import static com.ppdai.das.client.SegmentConstants.INNER_JOIN;
import static com.ppdai.das.client.SegmentConstants.INSERT;
import static com.ppdai.das.client.SegmentConstants.INTO;
import static com.ppdai.das.client.SegmentConstants.JOIN;
import static com.ppdai.das.client.SegmentConstants.LEFT_JOIN;
import static com.ppdai.das.client.SegmentConstants.NOT;
import static com.ppdai.das.client.SegmentConstants.ON;
import static com.ppdai.das.client.SegmentConstants.OR;
import static com.ppdai.das.client.SegmentConstants.ORDER_BY;
import static com.ppdai.das.client.SegmentConstants.PLACE_HOLDER;
import static com.ppdai.das.client.SegmentConstants.RIGHT_JOIN;
import static com.ppdai.das.client.SegmentConstants.SELECT;
import static com.ppdai.das.client.SegmentConstants.SET;
import static com.ppdai.das.client.SegmentConstants.UPDATE;
import static com.ppdai.das.client.SegmentConstants.USING;
import static com.ppdai.das.client.SegmentConstants.VALUES;
import static com.ppdai.das.client.SegmentConstants.WHERE;
import static com.ppdai.das.client.SegmentConstants.comma;
import static com.ppdai.das.client.SegmentConstants.expression;
import static com.ppdai.das.client.SegmentConstants.leftBracket;
import static com.ppdai.das.client.SegmentConstants.rightBracket;
import static com.ppdai.das.client.SegmentConstants.text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.ppdai.das.client.sqlbuilder.AbstractColumn;
import com.ppdai.das.client.sqlbuilder.BuilderContext;
import com.ppdai.das.client.sqlbuilder.ColumnName;
import com.ppdai.das.client.sqlbuilder.ColumnOrder;
import com.ppdai.das.client.sqlbuilder.ColumnReference;
import com.ppdai.das.client.sqlbuilder.ConditionBuilder;
import com.ppdai.das.client.sqlbuilder.DefaultBuilderContext;
import com.ppdai.das.client.sqlbuilder.Expression;
import com.ppdai.das.client.sqlbuilder.MeltdownHelper;
import com.ppdai.das.client.sqlbuilder.Page;
import com.ppdai.das.client.sqlbuilder.ParameterDefinitionProvider;
import com.ppdai.das.client.sqlbuilder.ParameterProvider;
import com.ppdai.das.client.sqlbuilder.TableDeclaration;
import com.ppdai.das.client.sqlbuilder.Template;
import com.ppdai.das.client.sqlbuilder.Text;
import com.ppdai.das.service.EntityMeta;
import com.ppdai.das.strategy.ConditionList;

/**
 * This is a builder regardless the differnece of dbtype 
 * @author hejiehui
 *
 */
public class SqlBuilder implements Segment, ParameterProvider, ParameterDefinitionProvider {
    private MeltdownHelper meltdownHelper = new MeltdownHelper();
    private LinkedList<Segment> segments = new LinkedList<>();
    private boolean withLock;
    private Hints hints;
    private Class<?> clazz;
    private EntityMeta entityMeta;
    private boolean selectCount;

    private List<Parameter> builtParameters = new ArrayList<>();
    private List<ParameterDefinition> builtDefinitions = new ArrayList<>();

    private static final int NO_LIMIT = -1;
    private static volatile int limitThreshold = NO_LIMIT;

    public boolean isWithLock() {
        return withLock;
    }

    public boolean isSelectCount() {
        return selectCount;
    }

    public SqlBuilder withLock() {
        this.withLock = true;
        return this;
    }

    public EntityMeta getEntityMeta() {
        return entityMeta;
    }

    public SqlBuilder setEntityMeta(EntityMeta entityMeta) {
        this.entityMeta = entityMeta;
        return this;
    }

    /**
	 * Add segments to builder
	 * 
	 * @param segs parameter that is not of type Segment will be added as Text with the value of toString()
	 * @return current builder
	 */
    public SqlBuilder append(Object...segs) {
	    for(Object seg: segs)
	        add(seg);

	    return this;
	}

    public SqlBuilder appendTemplate(String template, Parameter...parameters) {
        return append(new Template(template, parameters));
    }
    
    public SqlBuilder appendBatchTemplate(String template, ParameterDefinition...parameterDefinitions) {
        return append(new Template(template, parameterDefinitions));
    }

    /**
     * Add segments when required condition is met.
     *  
     * @param required condition to meet
     * @param segs parameter that is not of type Segment will be added as Text with the value of toString()
     * @return current builder
     */
    public SqlBuilder appendWhen(boolean required, Object...segs) {
        return required ? append(segs) : this;
    }

    public SqlBuilder appendTemplateWhen(boolean required, String template, Parameter...parameters) {
        return required ? appendTemplate(template, parameters) : this;
    }
    
    public SqlBuilder appendBatchTemplateWhen(boolean required, String template, ParameterDefinition...parameterDefinitions) {
        return required ? appendBatchTemplate(template, parameterDefinitions) : this;
    }

    /**
     * Add segments separated by given separator
     * 
     * @param separator
     * @param segs
     * @return
     */
    public SqlBuilder appendWith(Text separator, Object...segs) {
        for(Object seg: segs)
            append(seg, separator);

        if(segs.length > 0) segments.removeLast();
        return this;
    }
    
    public SqlBuilder appendWithWhen(boolean required, Text separator, Object...segs) {
        return required ? appendWith(separator, segs) : this;
    }
    
    public SqlBuilder appendPlaceHolder(int count) {
        for (int i = 0; i < count; i++)
            append(PLACE_HOLDER, COMMA);

        if(count > 0) segments.removeLast();
        return this;
    }


    public SqlBuilder appendPlaceHolderWhen(boolean required, int count) {
        return required ? appendPlaceHolder(count) : this;
    }
    
    public static SqlBuilder selectAll() {
        return new SqlBuilder().append(SELECT, "*");
    }

    public static SqlBuilder selectCount() {
        SqlBuilder builder = new SqlBuilder().append(SELECT, "count(1)").intoObject();
        builder.selectCount = true;
        return builder;
    }
    
    public static SqlBuilder select(Object...columns) {
        return new SqlBuilder().append(SELECT, comma(columns));
    }
    
    public static SqlBuilder selectDistinct(Object...columns) {
        return new SqlBuilder().append(SELECT, DISTINCT, comma(columns));
    }
    
    public static SqlBuilder selectTop(int count, Object...columns) {
        return new SqlBuilder().append(SELECT).top(count).append(comma(columns));
    }
    
    public SqlBuilder from(Object...tables) {
        for(int i = 0; i < tables.length;i++) {
            tables[i] = TableDeclaration.filter(tables[i]);
        }
            
        return append(FROM, comma(tables));
    }
    
    public static SqlBuilder selectAllFrom(TableDefinition table) {
        return select((Object[])table.allColumns()).from(table);
    }
    
    /**
     * Be careful about using insert when sharded by db, table or both. By default, it will apply to all 
     * shards that meet the criterion. That means you can insert more than just one recorder by using 
     * a single insert. E.g.: if you have a logic DB that has 4 DB shards, each DB shard has 4 table shards.
     * If a insert is used and the condition or parameter implies that it fit all DB and table shards, there 
     * will be 4 *4 recorders inserted at last. 
     * 
     * 
     * @param table
     * @param columns
     * @return
     */
    public static SqlBuilder insertInto(TableDefinition table, ColumnDefinition...columns) {
        String[] columnNames = new String[columns.length];
        int i = 0;
        for(ColumnDefinition def: columns)
            columnNames[i++] = def.getColumnName();
        return insertInto(table, columnNames);
    }
    
    public static SqlBuilder insertInto(TableDefinition table, String...columns) {
        return insertInto(table).bracket(comma((Object[])columns));
    }
    
    public static SqlBuilder insertInto(TableDefinition table) {
        return new SqlBuilder().append(INSERT, INTO, table);
    }
    
    public SqlBuilder values(Object...segs) {
        return append(VALUES).bracket(comma(segs));
    }
    
    public static SqlBuilder update(TableDefinition table) {
        return new SqlBuilder().append(UPDATE, table);
    }
    
    public SqlBuilder set(Object...segs) {
       Object[] excludeNotIncluded = Arrays.stream(segs).filter(seg ->{
            if(seg instanceof Expression) {
                if (!((Expression)seg).isIncluded()){
                    return false;
                }
            }
            return true;
        }).toArray();
        return append(SET, comma(excludeNotIncluded));
    }
    
    public static SqlBuilder deleteFrom(Object table) {
        return new SqlBuilder().append(DELETE, FROM).append(table);
    }

    public SqlBuilder join(Object...segs) {
        return append(JOIN).append(segs);
    }
    
    public SqlBuilder innerJoin(Object...segs) {
        return append(INNER_JOIN).append(segs);
    }
    
    /**
     * Are you sure?
     */
    public SqlBuilder fullJoin(Object...segs) {
        return append(FULL_JOIN).append(segs);
    }
    
    public SqlBuilder leftJoin(Object...segs) {
        return append(LEFT_JOIN).append(segs);
    }
    
    public SqlBuilder rightJoin(Object...segs) {
        return append(RIGHT_JOIN).append(segs);
    }
    
    /**
     * Are you sure?
     */
    public SqlBuilder crossJoin(Object...segs) {
        return append(CROSS_JOIN).append(segs);
    }
    
    public SqlBuilder on(Object...conditions) {
        return append(ON).append(conditions);
    }
    
    public SqlBuilder using(AbstractColumn column) {
        return append(USING).bracket(new ColumnName(column));
    }
    
    public SqlBuilder where(Object...conditions) {
        return append(WHERE).append(conditions);
    }
    
    public SqlBuilder groupBy(Object...columns) {
        return append(GROUP_BY, comma(replaceByReference(columns)));
    }

    private static Object[] replaceByReference(Object... columns) {
        Object[] replaced = new Object[columns.length];
        for(int i = 0; i < columns.length; i++) {
            Object o = columns[i];
            replaced[i] = o instanceof AbstractColumn ? new ColumnReference((AbstractColumn)o) : o;
        }
        return replaced;
    }
    
    public SqlBuilder having(Object...conditions) {
        return append(HAVING).append(conditions);
    }
    
    public SqlBuilder orderBy(Object...columns) {
        append(ORDER_BY);
        Object[] replaced = new Object[columns.length];
        for(int i = 0; i < columns.length; i++) {
            Object o = columns[i];
            replaced[i] = o instanceof AbstractColumn ? new ColumnOrder((AbstractColumn)o, true) : o;
        }
        return appendWith(COMMA, replaced);
    }
    
    public SqlBuilder limit(int start, int count) {
        checkLimitThreshold(count);
        return appendTemplate("LIMIT ?, ?", Parameter.integerOf("", start), Parameter.integerOf("", count));
    }
    
    public SqlBuilder limit(ParameterDefinition start, ParameterDefinition count) {
        if (count instanceof Parameter) {
            Parameter parameter = (Parameter) count;
            checkLimitThreshold((int)parameter.getValue());
        }
        return appendBatchTemplate("LIMIT ?, ?", start, count);
    }

    public SqlBuilder limit(int count) {
        checkLimitThreshold(count);
        return appendTemplate("LIMIT ?", Parameter.integerOf("", count));
    }
    
    public SqlBuilder limit(ParameterDefinition count) {
        if (count instanceof Parameter) {
            Parameter parameter = (Parameter) count;
            checkLimitThreshold((int)parameter.getValue());
        }
        return appendBatchTemplate("LIMIT ?", count);
    }
    
    public SqlBuilder offset(int start, int count) {
        checkLimitThreshold(count);
        return appendTemplate("OFFSET ? ROWS FETCH NEXT ? ROWS ONLY", Parameter.integerOf("", start), Parameter.integerOf("", count));
    }
    
    public SqlBuilder offset(ParameterDefinition start, ParameterDefinition count) {
        if (count instanceof Parameter) {
            Parameter parameter = (Parameter) count;
            checkLimitThreshold((int)parameter.getValue());
        }
        return appendBatchTemplate("OFFSET ? ROWS FETCH NEXT ? ROWS ONLY", start, count);
    }
    
    public SqlBuilder top(int count) {
        // For MS Sql Server, you can not specify count of TOP as parameter
        checkLimitThreshold(count);
        return append(new Template("TOP " + count));
    }
    
    /**
     * General paging. It will be replaced with limit or offset clause depends on DB type.
     * 
     * Note:
     * This api does not have an overwrite version of ParameterDefinition because 
     * actual parameter used in real sql needs to be calculated from page no and page size.
     * 
     * @param pageNo page index, start from 1
     * @param pageSize how many recorders in a page
     * @return
     */
    public SqlBuilder atPage(int pageNo, int pageSize) {
        checkLimitThreshold(pageSize);
        return append(new Page(pageNo, pageSize));
    }
    
    public SqlBuilder leftBracket() {
        return append(leftBracket);
    }
    
    public SqlBuilder rightBracket() {
        return append(rightBracket);
    }
    
    public SqlBuilder bracket(Object...segs) {
        return leftBracket().append((Object[])segs).rightBracket();
    }
    
    public SqlBuilder and() {
        return append(AND);
    }

    public SqlBuilder and(Object exp) {
        return append(AND, exp);
    }
    
    /**
     * @return exp1 AND exp2 AND exp3...
     */
    public SqlBuilder allOf(Object...exps) {
        return leftBracket().appendWith(AND, exps).rightBracket();
    }
    
    public SqlBuilder or() {
        return append(OR);
    }
    
    public SqlBuilder or(Object exp) {
        return append(OR, exp);
    }
    
    /**
     * @return exp1 OR exp2 OR exp3...
     */
    public SqlBuilder anyOf(Object...exps) {
        return leftBracket().appendWith(OR, exps).rightBracket();
    }
    
    public SqlBuilder not() {
        return append(NOT);
    }

    public SqlBuilder not(Object exp) {
        return append(NOT, exp);
    }
    
    /**
     * @return "1=1" AND. To adapt to most database. Must be followed with another expression to make sure AND works or removes properly
     */
    public SqlBuilder includeAll() {
        return append(expression("1=1"), AND);
    }
    
    /**
     * @return "1<>1" OR. To adapt to most database. Must be followed with another expression to make sure AND works or removes properly
     */
    public SqlBuilder excludeAll() {
        return append(expression("1<>1"), OR);
    }
    
	public Hints hints() {
        return hints == null ? hints = new Hints() : hints;
	}
	
    public SqlBuilder setHints(Hints hints) {
        this.hints = hints;
        return this;
    }

    public <T> SqlBuilder into(Class<T> clazz) {
	    this.clazz = clazz;
	    return this;
	}
	
    public <T> SqlBuilder intoObject() {
        this.clazz = Object.class;
        return this;
    }

    public <T> SqlBuilder intoMap() {
        this.clazz = Map.class;
        return this;
    }

    public Class<?> getEntityType() {
	    return clazz;
	}

    @Override
    public String build(BuilderContext context) {
        return meltdownHelper.build(segments, context);
    }

    public String toString() {
        return build(new DefaultBuilderContext());
    }

    @Override
    public List<ParameterDefinition> buildDefinitions() {
        if(builtDefinitions.isEmpty()) {
            List<ParameterDefinition> pdList = new ArrayList<>();
            List<Segment> filtered = getFilteredSegments();

            for(Segment seg: filtered) {
                if(seg instanceof ParameterDefinitionProvider)
                    pdList.addAll(((ParameterDefinitionProvider)seg).buildDefinitions());
            }
            return pdList;
        } else {
            return builtDefinitions;
        }
    }

    @Override
    public List<Parameter> buildParameters() {
        List<Parameter> params;
        if(builtParameters.isEmpty()) {
            params = new ArrayList<>();
            List<Segment> filtered = getFilteredSegments();

            for(Segment seg: filtered) {
                if(seg instanceof ParameterProvider)
                    params.addAll(((ParameterProvider)seg).buildParameters());
            }
        }else {
            params = builtParameters;
        }
        
        return Parameter.reindex(params);
    }

    public SqlBuilder setBuiltParameters(List<Parameter> builtParameters) {
        this.builtParameters = builtParameters;
        return this;
    }

    public SqlBuilder setBuiltDefinitions(List<ParameterDefinition> builtDefinitions) {
        this.builtDefinitions = builtDefinitions;
        return this;
    }

    private SqlBuilder add(Object seg) {
        Objects.requireNonNull(seg);
        
        if(seg instanceof Object[])
            segments.addAll(split((Object[])seg));
        else
            segments.add(normalize(seg));

        return this;
    }
    
    private List<Segment> split(Object[] objs) {
        List<Segment> l = new ArrayList<>();
        for(Object obj: objs) {
            if(obj instanceof Object[])
                l.addAll(split((Object[])obj));
            else
                l.add(normalize(obj));
        }
        return l;
    }

    private Segment normalize(Object seg) {
        return seg instanceof Segment ? (Segment)seg : text(seg.toString());
    }

    public List<Segment> getFilteredSegments(){
        return meltdownHelper.meltdown(segments);
    }
    
    public ConditionList buildQueryConditions() {
        return new ConditionBuilder().buildQueryConditions(getFilteredSegments());
    }
    
    public ConditionList buildUpdateConditions() {
        return new ConditionBuilder().buildUpdateConditions(getFilteredSegments());
    }

    private void checkLimitThreshold(int limit) {
        if(limitThreshold != NO_LIMIT && limit >= limitThreshold){
            throw new IllegalArgumentException(
                    "limit or top exceed threshold. limit: " + limit + ", limitThreshold: " + limitThreshold);
        }
    }

    public LinkedList<Segment> getSegments() {
        return segments;
    }

    public static void setLimitThreshold(int threshold) {
        limitThreshold = threshold;
    }
}
