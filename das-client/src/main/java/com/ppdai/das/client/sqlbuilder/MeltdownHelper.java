package com.ppdai.das.client.sqlbuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.ppdai.das.client.Segment;
import com.ppdai.das.client.SegmentConstants;
import com.ppdai.das.client.SqlBuilder;

public class MeltdownHelper implements SegmentConstants {
    private boolean enableSmartSpaceSkipping = true;

    public String build(List<Segment> segments, BuilderContext context) {
        return concat(meltdown(segments), context);
    }

    public List<Segment> meltdown(List<Segment> segments) {
        segments = new LinkedList<>(segments);
        LinkedList<Segment> filtered = new LinkedList<>();
        
        for(Segment entry: segments) {
            if(isExpression(entry) && isNull(entry)){
                meltDownNullValue(filtered);
                continue;
            }

            if(isBracket(entry) && !isLeft(entry)){
                if(meltDownRightBracket(filtered))
                    continue;
            }
            
            // AND/OR
            if(isOperator(entry) && !isNot(entry)) {
                if(meltDownAndOrOperator(filtered))
                    continue;
            }
            
            if(entry instanceof Includable && ((Includable)entry).isIncluded() == false)
                continue;
            
            if(entry instanceof SqlBuilder) {
                filtered.addAll(((SqlBuilder)entry).getFilteredSegments());
            }else
                filtered.add(entry);
        }
        
        return filtered;
    }
    
    /**
     * If there is COMMA, then the leading space will not be appended.
     * If there is bracket, then both leading and trailing space will be omitted.
     * 
     * @param segments
     * @return
     * @throws SQLException
     */
    private String concat(List<Segment> segments, BuilderContext context) {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < segments.size(); i ++) {
            Segment curSegment = segments.get(i);
            Segment nextSegment = (i == segments.size() - 1) ? null: segments.get(i+1);

            sb.append(curSegment.build(context));

            if(skipSpaceInsertion(curSegment, nextSegment))
                continue;
            
            sb.append(" ");
        }
        
        return sb.toString().trim();
    }

    /**
     * Builder will not insert space if enableSmartSpaceSkipping is enabled and:
     * 1. current cuase is operator(AND, OR, NOT)
     * 2. current cuase is left bracket
     * 3. next segment is right bracket or COMMA
     */
    private boolean skipSpaceInsertion(Segment curSegment, Segment nextSegment) {
        if(!enableSmartSpaceSkipping)
            return false;
        
        if(isOperator(curSegment))
            return false;
        // if after "("
        if(isBracket(curSegment) && isLeft(curSegment))
            return true;
        
        // reach the end
        if(nextSegment == null)
            return true;

        if(isBracket(nextSegment) && !isLeft(nextSegment))
            return true;
        
        return isComma(nextSegment);
    }
    
    private void meltDownNullValue(LinkedList<Segment> filtered) {
        if(filtered.isEmpty())
            return;

        while(!filtered.isEmpty()) {
            Segment entry = filtered.getLast();
            // Remove any leading AND/OR/NOT (NOT is both operator and segment)
            if(isOperator(entry)) {
                filtered.removeLast();
            }else
                break;
        }
    }

    private boolean meltDownRightBracket(LinkedList<Segment> filtered) {
        int bracketCount = 1;
        while(!filtered.isEmpty()) {
            Segment entry = filtered.getLast();
            // One ")" only remove one "("
            if(isBracket(entry) && isLeft(entry) && bracketCount == 1){
                filtered.removeLast();
                bracketCount--;
            } else if(isOperator(entry)) {// Remove any leading AND/OR/NOT (NOT is both operator and segment)
                filtered.removeLast();
            } else
                break;
        }
        
        return bracketCount == 0? true : false;
    }

    private boolean meltDownAndOrOperator(LinkedList<Segment> filtered) {
        // If it is the first element
        if(filtered.isEmpty())
            return true;

        Segment entry = filtered.getLast();

        // The last one is "("
        if(isBracket(entry))
            return isLeft(entry);
            
        // AND/OR/NOT AND/OR
        if(isOperator(entry)) {
            return true;
        }
        
        // If it is expression. 
        if(isExpression(entry))
            return false;

        // Reach the beginning of the meltdown section
        return true;
    }
    /**
     * @return if current segment is comma
     */
    private boolean isComma(Segment segment) {
        return segment == COMMA;
    }

    /**
     * @return if current segment is an expression
     */
    private boolean isExpression(Segment segment) {
        return segment instanceof Expression;
    }

    /**
     * @return if current segment is null
     */
    private boolean isNull(Segment segment) {
        Expression exp = (Expression)segment;
        return !exp.isIncluded();
    }
    
    /**
     * @return if current segment is a bracket
     */
    private boolean isBracket(Segment segment) {
        return segment instanceof Bracket;
    }
    
    /**
     * @return if current segment is left bracket
     */
    private boolean isLeft(Segment segment) {
        return segment == leftBracket;
    }
    
    /**
     * @return if current segment is an operator
     */
    private boolean isOperator(Segment segment) {
        return segment instanceof Operator;
    }
    
    /**
     * @return if current segment is NOT operator
     */
    private boolean isNot(Segment segment) {
        return segment == NOT;
    }
}
