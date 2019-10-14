package com.ppdai.das.client.sqlbuilder;

import static com.ppdai.das.client.SegmentConstants.AND;
import static com.ppdai.das.client.SegmentConstants.NOT;
import static com.ppdai.das.client.SegmentConstants.OR;
import static com.ppdai.das.client.SegmentConstants.bracket;
import static com.ppdai.das.strategy.OperatorEnum.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.ppdai.das.client.SqlBuilder;
import com.ppdai.das.strategy.ColumnCondition;
import com.ppdai.das.strategy.Condition;
import com.ppdai.das.strategy.ConditionList;
import com.ppdai.das.strategy.OperatorEnum;

public class RangeProviderBuilderHelperTest {
    private void assertProvider(Condition provider, OperatorEnum op) {
        assertNotNull(provider);
        if(provider instanceof ColumnCondition)
            assertTrue(op == ((ColumnCondition)provider).getOperator());
        else {
            ConditionList list = (ConditionList)provider;
            assertTrue(list.size() == 1);
            assertProvider(list.getLast(), op);
        }
    }

    private void assertProviderList(Condition provider, int size, boolean intersected) {
        ConditionList list = (ConditionList)provider;
        assertTrue(list.size() == size);
        assertTrue(list.isIntersected() == intersected);
    }

    private void assertProviderList(Condition provider, int size, boolean intersected, OperatorEnum op) {
        assertProviderList(provider, size, intersected);
        ConditionList list = (ConditionList)provider;
        for(Condition c: list)
            assertProvider(c, op);
    }

    @Test
    public void testSingle() {
        Person.PersonDefinition p = Person.PERSON;
        SqlBuilder sb = SqlBuilder.selectAllFrom(p).where(p.CityID.eq(1));
        assertProvider(sb.buildQueryConditions(), EQUAL);
        
        sb = SqlBuilder.selectAllFrom(p).where(p.CityID.neq(1));
        assertProvider(sb.buildQueryConditions(), NOT_EQUAL);
        
        sb = SqlBuilder.selectAllFrom(p).where(p.CityID.lt(1));
        assertProvider(sb.buildQueryConditions(), LESS_THAN);
        
        sb = SqlBuilder.selectAllFrom(p).where(p.CityID.lteq(1));
        assertProvider(sb.buildQueryConditions(), OperatorEnum.LESS_THAN_OR_EQUAL);
        
        sb = SqlBuilder.selectAllFrom(p).where(p.CityID.gt(1));
        assertProvider(sb.buildQueryConditions(), GREATER_THAN);
        
        sb = SqlBuilder.selectAllFrom(p).where(p.CityID.gteq(1));
        assertProvider(sb.buildQueryConditions(), GREATER_THAN_OR_EQUAL);
        
        List<Object> values = new ArrayList<>();
        
        sb = SqlBuilder.selectAllFrom(p).where(p.CityID.in(values));
        assertProvider(sb.buildQueryConditions(), IN);
        
        sb = SqlBuilder.selectAllFrom(p).where(p.CityID.notIn(values));
        assertProvider(sb.buildQueryConditions(), NOT_IN);
        
        sb = SqlBuilder.selectAllFrom(p).where(p.CityID.between(1, 2));
        assertProvider(sb.buildQueryConditions(), BEWTEEN);
        
        sb = SqlBuilder.selectAllFrom(p).where(p.CityID.notBetween(1, 2));
        assertProvider(sb.buildQueryConditions(), NOT_BETWEEN);
    }

    @Test
    public void testNotSingle() {
        Person.PersonDefinition p = Person.PERSON;
        SqlBuilder sb = SqlBuilder.selectAllFrom(p).where().not(p.CityID.eq(1));
        assertProvider(sb.buildQueryConditions(), NOT_EQUAL);
        
        sb = SqlBuilder.selectAllFrom(p).where().not(p.CityID.neq(1));
        assertProvider(sb.buildQueryConditions(), EQUAL);
        
        sb = SqlBuilder.selectAllFrom(p).where().not(p.CityID.lt(1));
        assertProvider(sb.buildQueryConditions(), GREATER_THAN_OR_EQUAL);
        
        sb = SqlBuilder.selectAllFrom(p).where().not(p.CityID.lteq(1));
        assertProvider(sb.buildQueryConditions(), GREATER_THAN);
        
        sb = SqlBuilder.selectAllFrom(p).where().not(p.CityID.gt(1));
        assertProvider(sb.buildQueryConditions(), LESS_THAN_OR_EQUAL);
        
        sb = SqlBuilder.selectAllFrom(p).where().not(p.CityID.gteq(1));
        assertProvider(sb.buildQueryConditions(), LESS_THAN);
        
        List<Object> values = new ArrayList<>();
        
        sb = SqlBuilder.selectAllFrom(p).where().not(p.CityID.in(values));
        assertProvider(sb.buildQueryConditions(), NOT_IN);
        
        sb = SqlBuilder.selectAllFrom(p).where().not(p.CityID.notIn(values));
        assertProvider(sb.buildQueryConditions(), IN);
        
        sb = SqlBuilder.selectAllFrom(p).where().not(p.CityID.between(1, 2));
        assertProvider(sb.buildQueryConditions(), NOT_BETWEEN);
        
        sb = SqlBuilder.selectAllFrom(p).where().not(p.CityID.notBetween(1, 2));
        assertProvider(sb.buildQueryConditions(), BEWTEEN);
    }

    @Test
    public void testNotNotSingle() {
        Person.PersonDefinition p = Person.PERSON;
        SqlBuilder sb = SqlBuilder.selectAllFrom(p).where().not().not(p.CityID.eq(1));
        assertProvider(sb.buildQueryConditions(), EQUAL);
        
        sb = SqlBuilder.selectAllFrom(p).where().not().not(p.CityID.neq(1));
        assertProvider(sb.buildQueryConditions(), NOT_EQUAL);
        
        sb = SqlBuilder.selectAllFrom(p).where().not().not(p.CityID.lt(1));
        assertProvider(sb.buildQueryConditions(), LESS_THAN);
        
        sb = SqlBuilder.selectAllFrom(p).where().not().not(p.CityID.lteq(1));
        assertProvider(sb.buildQueryConditions(), OperatorEnum.LESS_THAN_OR_EQUAL);
        
        sb = SqlBuilder.selectAllFrom(p).where().not().not(p.CityID.gt(1));
        assertProvider(sb.buildQueryConditions(), GREATER_THAN);
        
        sb = SqlBuilder.selectAllFrom(p).where().not().not(p.CityID.gteq(1));
        assertProvider(sb.buildQueryConditions(), GREATER_THAN_OR_EQUAL);
        
        List<Object> values = new ArrayList<>();
        
        sb = SqlBuilder.selectAllFrom(p).where().not().not(p.CityID.in(values));
        assertProvider(sb.buildQueryConditions(), IN);
        
        sb = SqlBuilder.selectAllFrom(p).where().not().not(p.CityID.notIn(values));
        assertProvider(sb.buildQueryConditions(), NOT_IN);
        
        sb = SqlBuilder.selectAllFrom(p).where().not().not(p.CityID.between(1, 2));
        assertProvider(sb.buildQueryConditions(), BEWTEEN);
        
        sb = SqlBuilder.selectAllFrom(p).where().not().not(p.CityID.notBetween(1, 2));
        assertProvider(sb.buildQueryConditions(), NOT_BETWEEN);
    }

    @Test
    public void testAandB() {
        Person.PersonDefinition p = Person.PERSON;
        SqlBuilder sb = SqlBuilder.selectAllFrom(p).where(p.CityID.eq(1), AND, p.CountryID.eq(2));
        ConditionList provider = (ConditionList)sb.buildQueryConditions();
        assertProviderList(provider, 2, true, EQUAL);
    }

    @Test
    public void testNotAandB() {
        Person.PersonDefinition p = Person.PERSON;
        SqlBuilder sb = SqlBuilder.selectAllFrom(p).where().not(bracket(p.CityID.eq(1), AND, p.CountryID.eq(2)));
        ConditionList provider = (ConditionList)sb.buildQueryConditions();
        assertProviderList(provider, 2, false, NOT_EQUAL);
    }

    @Test
    public void testNotNotAandB() {
        Person.PersonDefinition p = Person.PERSON;
        SqlBuilder sb = SqlBuilder.selectAllFrom(p).where(NOT, bracket(NOT, bracket(p.CityID.eq(1), AND, p.CountryID.eq(2))));
        ConditionList provider = (ConditionList)sb.buildQueryConditions();
        assertProviderList(provider, 2, true, EQUAL);
    }

    @Test
    public void testAorB() {
        Person.PersonDefinition p = Person.PERSON;
        SqlBuilder sb = SqlBuilder.selectAllFrom(p).where(p.CityID.eq(1), OR, p.CountryID.eq(2));
        ConditionList provider = (ConditionList)sb.buildQueryConditions();
        assertProviderList(provider, 2, false, EQUAL);
    }

    @Test
    public void testNotAorB() {
        Person.PersonDefinition p = Person.PERSON;
        SqlBuilder sb = SqlBuilder.selectAllFrom(p).where().not(bracket(p.CityID.eq(1), OR, p.CountryID.eq(2)));
        ConditionList provider = (ConditionList)sb.buildQueryConditions();
        assertProviderList(provider, 2, true, NOT_EQUAL);
    }

    @Test
    public void testNotNotAorB() {
        Person.PersonDefinition p = Person.PERSON;
        SqlBuilder sb = SqlBuilder.selectAllFrom(p).where().not(bracket(NOT, bracket(p.CityID.eq(1), OR, p.CountryID.eq(2))));
        ConditionList provider = (ConditionList)sb.buildQueryConditions();
        assertProviderList(provider, 2, false, EQUAL);
    }

//    @Test
//    public void testAandBorCandD() {
//        Person.PersonDefinition p = Person.PERSON;
//        SqlBuilder sb = SqlBuilder.selectAllFrom(p).where(p.CityID.eq(1), AND, p.CityID.eq(2), OR, p.CountryID.eq(2), AND, p.DataChange_LastTime.eq(2));
//        ConditionList provider = (ConditionList)sb.buildCondition();
//        assertNotNull(provider);
//        assertProviderList(provider, 2, false);
//        assertFalse(provider.isIntersected());
//        
//        List<Condition> rpList = provider.getConditions();
//        assertProviderList(rpList.get(0), 2, true);
//        assertProviderList(rpList.get(1), 2, true);
//    }
//    
//    @Test
//    public void testNot() {
//        Person.PersonDefinition p = Person.PERSON;
//        SqlBuilder sb;
//        Condition provider;
//        
//        sb = SqlBuilder.selectAllFrom(p).where(NOT, p.CityID.eq(1));
//        provider = sb.buildCondition();
//        assertProvider(provider, true);
//        
//        sb = SqlBuilder.selectAllFrom(p).where(NOT, NOT, p.CityID.eq(1));
//        provider = sb.buildCondition();
//        assertProvider(provider, false);
//        
//        sb = SqlBuilder.selectAllFrom(p).where(NOT, NOT, NOT, p.CityID.eq(1));
//        provider = sb.buildCondition();
//        assertProvider(provider, true);
//        
//        sb = SqlBuilder.selectAllFrom(p).where(NOT, NOT, NOT, NOT, p.CityID.eq(1));
//        provider = sb.buildCondition();
//        assertProvider(provider, false);
//    }
//
//    @Test
//    public void testNot2() {
//        Person.PersonDefinition p = Person.PERSON;
//        SqlBuilder sb;
//        Condition provider;
//        ConditionList providers;
//        
//        sb = SqlBuilder.selectAllFrom(p).where(p.CityID.eq(1), AND, NOT, p.CountryID.eq(1));
//        providers = (ConditionList)sb.buildCondition();
//        assertProviderList(providers, 2, true, false);
//        
//        assertProvider(providers.get(0), false);
//        assertProvider(providers.get(1), true);
//        
//        sb = SqlBuilder.selectAllFrom(p).where(p.CityID.eq(1), AND, NOT, NOT, p.CountryID.eq(1));
//        providers = (ConditionList)sb.buildCondition();
//        assertProviderList(providers, 2, true, false);
//        
//        assertProvider(providers.get(0), false);
//        assertProvider(providers.get(1), false);
//        
//        sb = SqlBuilder.selectAllFrom(p).where(p.CityID.eq(1), AND, NOT, NOT, NOT, p.CountryID.eq(1));
//        providers = (ConditionList)sb.buildCondition();
//        assertProviderList(providers, 2, true, false);
//        
//        assertProvider(providers.get(0), false);
//        assertProvider(providers.get(1), true);
//    }
//
//    @Test
//    public void testNot3() {
//        Person.PersonDefinition p = Person.PERSON;
//        SqlBuilder sb;
//        Condition provider;
//        ConditionList providers;
//        
//        sb = SqlBuilder.selectAllFrom(p).where(p.CityID.eq(1), AND, NOT, bracket(p.CountryID.eq(1)));
//        providers = (ConditionList)sb.buildCondition();
//        assertProviderList(providers, 2, true, false);
//        
//        assertProvider(providers.get(0), false);
//        assertProvider(providers.get(1), true);
//        
//        sb = SqlBuilder.selectAllFrom(p).where(p.CityID.eq(1), AND, NOT, bracket(NOT, bracket(p.CountryID.eq(1))));
//        providers = (ConditionList)sb.buildCondition();
//        assertProviderList(providers, 2, true, false);
//        
//        assertProvider(providers.get(0), false);
//        assertProvider(providers.get(1), false);
//        
//        sb = SqlBuilder.selectAllFrom(p).where(p.CityID.eq(1), AND, NOT, bracket(NOT, bracket(NOT, bracket(p.CountryID.eq(1)))));
//        providers = (ConditionList)sb.buildCondition();
//        assertProviderList(providers, 2, true, false);
//        
//        assertProvider(providers.get(0), false);
//        assertProvider(providers.get(1), true);
//    }
//
//    @Test
//    public void testNot4() {
//        Person.PersonDefinition p = Person.PERSON;
//        SqlBuilder sb;
//        Condition provider;
//        ConditionList providers;
//        
//        sb = SqlBuilder.selectAllFrom(p).where(NOT, bracket(p.CountryID.eq(1)), AND, p.CityID.eq(1));
//        providers = (ConditionList)sb.buildCondition();
//        assertProviderList(providers, 2, true, false);
//        
//        assertProvider(providers.get(1), false);
//        assertProvider(providers.get(0), true);
//        
//        sb = SqlBuilder.selectAllFrom(p).where(NOT, bracket(NOT, bracket(p.CountryID.eq(1))), AND, p.CityID.eq(1));
//        providers = (ConditionList)sb.buildCondition();
//        assertProviderList(providers, 2, true, false);
//        
//        assertProvider(providers.get(1), false);
//        assertProvider(providers.get(0), false);
//        
//        sb = SqlBuilder.selectAllFrom(p).where(NOT, bracket(NOT, bracket(NOT, bracket(p.CountryID.eq(1)))), AND, p.CityID.eq(1));
//        providers = (ConditionList)sb.buildCondition();
//        assertProviderList(providers, 2, true, false);
//        
//        assertProvider(providers.get(1), false);
//        assertProvider(providers.get(0), true);
//    }
//
}
