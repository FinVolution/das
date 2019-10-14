package com.ppdai.das.client.sqlbuilder;

import static com.ppdai.das.client.SegmentConstants.leftBracket;
import static com.ppdai.das.client.SegmentConstants.rightBracket;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.ppdai.das.client.SqlBuilder;

import junit.framework.Assert;

public class MeltdownTest {

    @Test
    public void testEqual() throws SQLException {
        validate("equal", "T.[a] = ?");
        validate("equalNull", "");
        validate("equal AND equal", "T.[a] = ? AND T.[a] = ?");
        validate("equal AND equalNull", "T.[a] = ?");
        validate("equalNull AND equal", "T.[a] = ?");
        validate("equalNull AND equalNull", "");
        
        validate("( equal )", "(T.[a] = ?)");
        validate("( equalNull )", "");
        validate("( equal AND equal )", "(T.[a] = ? AND T.[a] = ?)");
        validate("( equal AND equalNull )", "(T.[a] = ?)");
        validate("( equalNull AND equal )", "(T.[a] = ?)");
        validate("( equalNull AND equalNull )", "");
    }
    
    @Test
    public void testLike() throws SQLException {
        validate("like", "T.[a] LIKE ?");
        validate("likeNull", "");
        validate("like AND like", "T.[a] LIKE ? AND T.[a] LIKE ?");
        validate("like AND likeNull", "T.[a] LIKE ?");
        validate("likeNull AND like", "T.[a] LIKE ?");
        validate("likeNull AND likeNull", "");
        
        validate("( like )", "(T.[a] LIKE ?)");
        validate("( likeNull )", "");
        validate("( like AND like )", "(T.[a] LIKE ? AND T.[a] LIKE ?)");
        validate("( like AND likeNull )", "(T.[a] LIKE ?)");
        validate("( likeNull AND like )", "(T.[a] LIKE ?)");
        validate("( likeNull AND likeNull )", "");
    }
    
    @Test
    public void testBetween() throws SQLException {
        validate("between", "T.[a] BETWEEN ? AND ?");
        validate("betweenNull", "");
        validate("between AND between", "T.[a] BETWEEN ? AND ? AND T.[a] BETWEEN ? AND ?");
        validate("between AND betweenNull", "T.[a] BETWEEN ? AND ?");
        validate("betweenNull AND between", "T.[a] BETWEEN ? AND ?");
        validate("betweenNull AND betweenNull", "");
        
        validate("( between )", "(T.[a] BETWEEN ? AND ?)");
        validate("( betweenNull )", "");
        validate("( between AND between )", "(T.[a] BETWEEN ? AND ? AND T.[a] BETWEEN ? AND ?)");
        validate("( between AND betweenNull )", "(T.[a] BETWEEN ? AND ?)");
        validate("( betweenNull AND between )", "(T.[a] BETWEEN ? AND ?)");
        validate("( betweenNull AND betweenNull )", "");
    }
    
    @Test
    public void testIsNull() throws SQLException {
        validate("isNull", "T.[a] IS NULL");
        validate("isNull AND isNull", "T.[a] IS NULL AND T.[a] IS NULL");
        
        validate("( isNull )", "(T.[a] IS NULL)");
        validate("( isNull AND isNull )", "(T.[a] IS NULL AND T.[a] IS NULL)");
    }
    
    @Test
    public void testIsNotNull() throws SQLException {
        validate("isNotNull", "T.[a] IS NOT NULL");
        validate("isNotNull AND isNotNull", "T.[a] IS NOT NULL AND T.[a] IS NOT NULL");
        
        validate("( isNotNull )", "(T.[a] IS NOT NULL)");
        validate("( isNotNull AND isNotNull )", "(T.[a] IS NOT NULL AND T.[a] IS NOT NULL)");
    }
    
    @Test
    public void testNotX() throws SQLException {
        validate("NOT equal", "NOT T.[a] = ?");
        validate("NOT equalNull", "");
        validate("NOT NOT NOT equal", "NOT NOT NOT T.[a] = ?");
        validate("NOT NOT NOT equalNull", "");
        validate("NOT equal AND NOT equal", "NOT T.[a] = ? AND NOT T.[a] = ?");
        validate("NOT equal AND NOT equalNull", "NOT T.[a] = ?");
        validate("NOT equalNull AND NOT equal", "NOT T.[a] = ?");
        validate("NOT equalNull AND NOT equalNull", "");
        
        validate("( NOT equal )", "(NOT T.[a] = ?)");
        validate("( NOT NOT NOT equal )", "(NOT NOT NOT T.[a] = ?)");
        validate("( NOT equalNull )", "");
        validate("( NOT NOT NOT equalNull )", "");
        validate("( NOT equal AND NOT equal )", "(NOT T.[a] = ? AND NOT T.[a] = ?)");
        validate("( NOT equal AND NOT equalNull )", "(NOT T.[a] = ?)");
        validate("( NOT equalNull AND NOT equal )", "(NOT T.[a] = ?)");
        validate("( NOT equalNull AND NOT equalNull )", "");
    }
    
    @Test
    public void testBracket() throws SQLException {
        validate("( ( equalNull ) )", "");
        validate("( ( ( equalNull ) ) )", "");
        validate("( ( ( equalNull ) ) ) AND ( ( ( equalNull ) ) )", "");
        validate("( ( ( equalNull ) ) ) OR ( ( ( equalNull ) ) )", "");
        validate("NOT ( NOT ( NOT ( NOT equalNull ) ) ) OR ( ( ( equalNull ) ) )", "");
    }
    
    @Test
    public void testOrX() throws SQLException {
        validate("equal OR equal", "T.[a] = ? OR T.[a] = ?");
        validate("equal AND ( equal OR equal )", "T.[a] = ? AND (T.[a] = ? OR T.[a] = ?)");
    }

    public void validate(String exp, String expected) throws SQLException {
        SqlBuilder builder = new SqlBuilder();
//        builder.from("People").setDatabaseCategory(DatabaseCategory.SqlServer);
        // equal equalNull between betweenNull in inNull like likeNull isNull isNotNull AND OR NOT ( )
        Table t = new Table("table").as("T");
        Column c= t.charColumn("[a]");
        String[] tokens = exp.split(" "); 
        for(String token: tokens) {
            switch (token) {
                case "equal":
                    builder.append(c.equal(""));
                    break;
                case "equalNull":
                    builder.append(c.equal(null).nullable());
                    break;
                case "like":
                    builder.append(c.like(""));
                    break;
                case "likeNull":
                    builder.append(c.like(null).nullable());
                    break;
                case "isNull":
                    builder.append(c.isNull());
                    break;
                case "isNotNull":
                    builder.append(c.isNotNull());
                    break;
                case "in":
                    List<?> l = new ArrayList<>();
                    builder.append(c.in(l));
                    break;
                case "between":
                    builder.append(c.between("", ""));
                    break;
                case "inNull":
                    List<?> lx = null;
                    builder.append(c.in(lx).nullable());
                    break;
                case "betweenNull":
                    builder.append(c.between(null, null).nullable());
                    break;
                case "AND":
                    builder.and();
                    break;
                case "OR":
                    builder.or();
                    break;
                case "NOT":
                    builder.not();
                    break;
                case "(":
                    builder.append(leftBracket);
                    break;
                case ")":
                    builder.append(rightBracket);
                    break;
                default:
                    Assert.fail("Unknown token: " + token);
            }
        }
        
        Assert.assertEquals(expected, build(builder));
    }
    
    private String build(SqlBuilder builder) {
        return builder.build(new DefaultBuilderContext());
    }

}
