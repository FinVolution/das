package com.ppdai.das.strategy;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ppdai.das.client.SegmentConstants;

/**
 * Represent top level where expression list or expression segment enclosed by ().
 * @author hejiehui
 *
 */
public class ConditionList implements Condition, Iterable<Condition> {
    /**
     * If all conditions joint by AND
     */
    private boolean intersected;
    
    private LinkedList<Condition> conditions = new LinkedList<>();

    public ConditionList(boolean intersected) {
        this.intersected = intersected;
    }
    
    public static ConditionList andList() {
        return new ConditionList(true);
    }
    
    public static ConditionList orList() {
        return new ConditionList(false);
    }
    
    public static ConditionList ofColumns(String tableName, Map<String, ?> pojo) {
        ConditionList condList = ConditionList.andList();
        for(Map.Entry<String, ?> entry: pojo.entrySet()) {
            //We omit empty fields
            if(entry.getValue() != null)
                condList.add(new ColumnCondition(OperatorEnum.EQUAL, tableName, entry.getKey(), entry.getValue()));
        }
        return condList;
    }
    
    public void add(Condition condition) {
        //Merge conditions with same intersection mode
        if(condition instanceof ConditionList && ((ConditionList)condition).isIntersected() == intersected)
            conditions.addAll(((ConditionList)condition).getConditions());
        else
            conditions.add(condition);
    }
    
    /**
     * @return are all conditions joint by AND
     */
    public boolean isIntersected() {
        return intersected;
    }
    
    public Condition get(int index) {
        return conditions.get(index);
    }

    public List<Condition> getConditions() {
        return conditions;
    }

    public Condition getLast() {
        return conditions.getLast();
    }
    
    public Condition removeLast() {
        return conditions.removeLast();
    }
    
    public int size() {
        return conditions.size();
    }

    @Override
    public Iterator<Condition> iterator() {
        return conditions.iterator();
    }

    public Set<String> getTables() {
        Set<String> tables = new HashSet<>();
        for(Condition c: conditions)
            tables.addAll(c.getTables());
        return tables;
    }

    @Override
    public Condition reverse() {
        intersected = !intersected;
        for(Condition c: conditions)
            c.reverse();
        return this;
    }
    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(intersected ? SegmentConstants.AND : SegmentConstants.OR).append("(");
        for(Condition c: conditions) {
            sb.append(c.toString()).append(",");
        }
        return sb.append(")").toString();
    }
}
