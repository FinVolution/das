package com.ppdai.das.client.sqlbuilder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import com.ppdai.das.client.Parameter;
import com.ppdai.das.client.Segment;
import com.ppdai.das.client.SegmentConstants;
import com.ppdai.das.strategy.ColumnCondition;
import com.ppdai.das.strategy.Condition;
import com.ppdai.das.strategy.ConditionList;
import com.ppdai.das.strategy.ConditionProvider;
import com.ppdai.das.strategy.OperatorEnum;

/**
 * Assumption, all expressions in where clause must using column definition, operator, NOT, ( or ).
 * TODO add shards to show diag; ignore SPACE in SQL  
 * 
 * @author hejiehui
 *
 */
public class ConditionBuilder {
    private static Set<Segment> expressionCandidates = new HashSet<>();
    private static Set<Segment> columnReferenceCandidates = new HashSet<>();
    
    static {
        expressionCandidates.add(SegmentConstants.leftBracket);
        expressionCandidates.add(SegmentConstants.rightBracket);
        expressionCandidates.add(SegmentConstants.AND);
        expressionCandidates.add(SegmentConstants.OR);
        expressionCandidates.add(SegmentConstants.NOT);

        columnReferenceCandidates.add(SegmentConstants.COMMA);
        columnReferenceCandidates.add(SegmentConstants.SPACE);
        columnReferenceCandidates.add(SegmentConstants.leftBracket);
        columnReferenceCandidates.add(SegmentConstants.rightBracket);
    }

    /**
     * Build range providers from expressions.
     * Stop at non expression segment or right bracket
     * TODO to consider expressions like p.name.eq("abc") AND "p.city = ?" AND ...
     * @param filtered
     * @return
     */
    public ConditionList buildQueryConditions(List<Segment> filtered) {
        // For top level , it will join all condition lists
        ConditionList providers = ConditionList.andList();
        
        List<Segment> conditionList = new ArrayList<>();
        for(Segment entry: filtered) {
            if(isConditionCandidate(entry))
                conditionList.add(entry);
            else
                parseCondition(providers, conditionList);
        }

        //For the potential last one
        parseCondition(providers, conditionList);

        return providers;

    }
    
    public ConditionList buildUpdateConditions(List<Segment> filtered) {
        /*
         * There are there kind of insert for mysql:
         * insert into table (column1, column2...) value/values (value list)
         * insert into table SET column1 = value1, column2 = value2, ...
         * insert into table select *
         * 
         * And every sql can be append with ON DUPLICATE KEY UPDATE assignment_lis :(
         */
        if(filtered.contains(SegmentConstants.INSERT)) {
            if(filtered.contains(SegmentConstants.SELECT) || filtered.contains(SegmentConstants.SET)) {
                filtered = filterColumnList(filtered);
                return buildQueryConditions(filtered);
            }
            
            return buildByParameters(filtered);
        }
        
        if(filtered.contains(SegmentConstants.DELETE)) {
            return buildQueryConditions(filtered);
        }
        
        /**
         * For UPDATE, the SET section will be union with WHERE section
         */
        if(filtered.contains(SegmentConstants.UPDATE)) {
            return buildQueryConditions(filtered);
        }
        
        return buildByParameters(filtered);
    }
    
    /**
     * Remove column declaration and bracket in insert SQL, so that we
     * can parse condition. E.g:
     * insert into person (person.name, person.Country) select .. where conditions
     * will be shorten as:
     * insert into person select .. where conditions
     * @param originalList
     * @return
     */
    private List<Segment> filterColumnList(List<Segment> originalList) {
        List<Segment> finalList = new LinkedList<>();
        int level = 0;
        List<Segment> bracketContents = new LinkedList<>();
        for(Segment entry: originalList) {
            if(isLeft(entry)) {
                level++;
                bracketContents.add(entry);
                continue;
            }

            if(isRight(entry)) {
                level--;
                bracketContents.add(entry);
                
                if(level > 0) 
                    continue;    

                //If it is not about column reference, we should add  back this content
                if(isColumnList(bracketContents) == false) {
                    finalList.addAll(bracketContents);
                }
                
                bracketContents.clear();
                continue;
            }

            if(level > 0) {
                bracketContents.add(entry);
            }else
                finalList.add(entry);
        }
        
        return finalList;
    }

    private boolean isColumnList(List<Segment> bracketContents) {
        // Check if it is all column reference
        boolean allMatch = true;
        for(Segment s: bracketContents) {
            if(!(s instanceof Text || columnReferenceCandidates.contains(s))) {
                allMatch = false;
                break;
            }
        }
        return allMatch;
    }

    private ConditionList buildByParameters(List<Segment> filtered) {
        //Extract all parameters into AND conditions
        ConditionList providers = ConditionList.andList();
        for(Segment entry: filtered) {
            if(entry instanceof Parameter) {
                Parameter p = (Parameter)entry;
                providers.add(new ColumnCondition(OperatorEnum.EQUAL, p.getTableName(), p.getName(), p.getValue()));
            }
        }
        return providers;
    }
    
    private void parseCondition(ConditionList providers, List<Segment> conditionList) {
        if(!conditionList.isEmpty()) {
            providers.add(parseCondition(conditionList));
            conditionList.clear();
        }
    }

    private Condition parseCondition(List<Segment> filtered) {
        LinkedList<Condition> providers = new LinkedList<>();
        Stack<Segment> stack = new Stack<>();
        
        for(Segment entry: filtered) {
            //The table column expressions
            if(isBuilder(entry)) {
                providers.add(((ConditionProvider)entry).build());
            }
            else if(isLeft(entry)){
                stack.push(entry);
            } 
            else if(isRight(entry)){
                while(!isLeft(entry = stack.pop())) {
                    combine(entry, providers);
                }
            } 
            else if(isAnd(entry)){
                Segment top = getTop(stack);
                if(top == null || isOr(top) || isLeft(top))
                    stack.push(entry);
                else {
                    combine(stack.pop(), providers);
                    stack.push(entry);
                }
            } 
            else if(isOr(entry)){
                Segment top = getTop(stack);
                if(top == null || isLeft(top))
                    stack.push(entry);
                else {
                    combine(stack.pop(), providers);
                    stack.push(entry);
                }
            }
            else if(isNot(entry)){
                stack.push(entry);
            }
        }
        
        while(!stack.isEmpty()) {
            Segment entry = stack.pop();
            combine(entry, providers);
        }
        
        return providers.getLast();
    }

    private Segment getTop(Stack<Segment> stack) {
        Segment top = stack.isEmpty() ? null : stack.peek();
        return top;
    }
    
    private void combine(Segment entry, LinkedList<Condition> providers) {
        if(isNot(entry)) {
            providers.getLast().reverse();
        }else {
            ConditionList temproviders = new ConditionList(isAnd(entry));
            
            Condition rp2 = providers.removeLast();
            Condition rp1 = providers.removeLast();
            
            temproviders.add(rp1);
            temproviders.add(rp2);
    
            providers.add(temproviders);
        }
    }
    
    /**
     * @return if current segment is an expression
     */
    private boolean isConditionCandidate(Segment segment) {
        return isBuilder(segment) || expressionCandidates.contains(segment);
    }
    
    private boolean isBuilder(Segment segment) {
        return segment instanceof ConditionProvider;
    }
    
    private boolean isLeft(Segment segment) {
        return segment == SegmentConstants.leftBracket;
    }
    
    private boolean isRight(Segment segment) {
        return segment == SegmentConstants.rightBracket;
    }
    
    private boolean isAnd(Segment segment) {
        return segment == SegmentConstants.AND;
    }
    
    private boolean isOr(Segment segment) {
        return segment == SegmentConstants.OR;
    }
    
    private boolean isNot(Segment segment) {
        return segment == SegmentConstants.NOT;
    }
}
