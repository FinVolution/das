package com.ppdai.das.util;

import com.google.common.collect.Lists;
import com.ppdai.das.client.delegate.remote.DasRemoteDelegate;
import com.ppdai.das.service.Entity;
import com.ppdai.das.service.EntityMeta;
import org.junit.Ignore;
import org.junit.Test;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Arrays;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ConvertUtilsTest {

    @Test @Ignore
    public void testPojo2EntityMySQL() {
        TesttableMySQL pojo = createMySQLPOJO();
        EntityMeta meta = DasRemoteDelegate.extract(pojo.getClass());
        List<Entity> entities = ConvertUtils.pojo2Entity(Arrays.asList(pojo), meta);
        List<Map<String, Object>> maps = ConvertUtils.entity2Map(entities, meta);

        assertEquals(1, maps.size());
        Map<String, Object> map = maps.get(0);

        assertEquals(pojo.getMyID(), map.get("MyID"));
        assertEquals(pojo.getMyBit(), map.get("MyBit"));
        assertEquals(pojo.getMyTinyint(), Integer.valueOf(map.get("MyTinyint").toString()));
        assertEquals(pojo.getMySmallint(), Integer.valueOf(map.get("MySmallint").toString()));
        assertEquals(pojo.getMyMediumint(), map.get("MyMediumint"));
        assertEquals(pojo.getMyBigint(), map.get("MyBigint"));
        assertEquals(pojo.getMyDecimal(), map.get("MyDecimal"));
        assertEquals(pojo.getMyFloat(), map.get("MyFloat"));
        assertEquals(pojo.getMyDouble(), map.get("MyDouble"));
        assertEquals(pojo.getMyBool(), map.get("MyBool"));
        assertEquals(pojo.getMyDate(), map.get("MyDate"));
        assertEquals(pojo.getMyDatetime(), map.get("MyDatetime"));
        assertEquals(pojo.getMyTimestamp(), map.get("MyTimestamp"));
        assertEquals(pojo.getMyTime(), map.get("MyTime"));
        assertEquals(pojo.getMyYear(), map.get("MyYear"));
        assertEquals(pojo.getMyVarchar(), map.get("MyVarchar"));
        assertEquals(pojo.getMyChar(), map.get("MyChar"));
        assertEquals(pojo.getMyText(), map.get("MyText"));
        assertArrayEquals(pojo.getMyBinary(), (byte[]) map.get("MyBinary"));
        assertArrayEquals(pojo.getMyVarbinary(), (byte[])map.get("MyVarbinary"));
        assertArrayEquals(pojo.getMyBlob(), (byte[])map.get("MyBlob"));
    }

    @Test  @Ignore
    public void testPojo2EntitySQLServer() {
        TesttableSQLServer pojo = createSQLServerPOJO();
        EntityMeta meta = DasRemoteDelegate.extract(pojo.getClass());
        List<Entity> entities = ConvertUtils.pojo2Entity(Arrays.asList(pojo), meta);
        List<Map<String, Object>> maps = ConvertUtils.entity2Map(entities, meta);

        assertEquals(1, maps.size());
        Map<String, Object> map = maps.get(0);

        assertEquals(pojo.getMyID(), map.get("MyID"));
        assertEquals(pojo.getMyBigint(), map.get("MyBigint"));
        assertEquals(pojo.getMyNumeric(), map.get("MyNumeric"));
        assertEquals(pojo.getMyBit(), map.get("MyBit"));
        assertEquals(pojo.getMySmallint(), Short.valueOf(map.get("MySmallint").toString()));
        assertEquals(pojo.getMyDecimal(), map.get("MyDecimal"));
        assertEquals(pojo.getMySmallmoney(), map.get("MySmallmoney"));
        assertEquals(pojo.getMyTinyint(), Short.valueOf(map.get("MyTinyint").toString()));
        assertEquals(pojo.getMyMoney(), map.get("MyMoney"));
        assertEquals(pojo.getMyFloat(), map.get("MyFloat"));
        assertEquals(pojo.getMyReal(), map.get("MyReal"));
        assertEquals(pojo.getMyDate(), map.get("MyDate"));
        assertEquals(pojo.getMyDatetime2(), map.get("MyDatetime2"));
        assertEquals(pojo.getMySmalldatetime(), map.get("MySmalldatetime"));
        assertEquals(pojo.getMyDatetime(), map.get("MyDatetime"));
        assertEquals(pojo.getMyTime(), map.get("MyTime"));
        assertEquals(pojo.getMyChar(), map.get("MyChar"));
        assertEquals(pojo.getMyText(), map.get("MyText"));
        assertArrayEquals(pojo.getMyBinary(), (byte[]) map.get("MyBinary"));
        assertArrayEquals(pojo.getMyVarbinary(), (byte[])map.get("MyVarbinary"));
        assertArrayEquals(pojo.getMyImage(), (byte[])map.get("MyImage"));
    }

    private TesttableMySQL createMySQLPOJO() {
        TesttableMySQL pojo = new TesttableMySQL();
        pojo.setMyBigint(18L);
        pojo.setMyBinary("MyBinary".getBytes());
        pojo.setMyBit(true);
        pojo.setMyBlob("MyBlob".getBytes());
        pojo.setMyChar("Mychar");
        pojo.setMyDate(new Date(1));
        pojo.setMyDatetime(new Timestamp(2));
        pojo.setMyDecimal(new BigDecimal(345.567));
        pojo.setMyDouble(4.1d);
        pojo.setMyFloat(5.1f);
        pojo.setMyID(6);
        pojo.setMyMediumint(7);
        pojo.setMySmallint(8);
        pojo.setMyText(null);//null test
        pojo.setMyTime(new Time(4));
        pojo.setMyTinyint(1);
        pojo.setMyVarbinary("varchBinary".getBytes());
        pojo.setMyVarchar("varch");
        pojo.setMyYear(new Date(5));
        pojo.setMyBool(true);
        pojo.setMyTimestamp(new Timestamp(5));

        return pojo;
    }

    private TesttableSQLServer createSQLServerPOJO() {
        TesttableSQLServer pojo = new TesttableSQLServer();

        pojo.setMyID(6);
        pojo.setMyBigint(18L);
        pojo.setMyBinary("MyBinary".getBytes());
        pojo.setMyNumeric(new BigDecimal(98.123));
        pojo.setMyBit(true);
        pojo.setMySmallmoney(new BigDecimal(9876.1234));
        pojo.setMyChar("Mychar");
        pojo.setMyDate(new Date(1));
        pojo.setMyDatetime(new Timestamp(2));
        pojo.setMyDatetime2(new Timestamp(200000));
        pojo.setMySmalldatetime(new Timestamp(300000));
        pojo.setMyDecimal(new BigDecimal(345.567));
        pojo.setMyFloat(5.1d);
        pojo.setMyReal(3.22f);
        pojo.setMySmallint((short)2);
        pojo.setMyText(null);//null test
        pojo.setMyTime(new Time(4));
        pojo.setMyTinyint((short)3);
        pojo.setMyMoney(new BigDecimal(100000.002));
        pojo.setMyVarbinary("varchBinary".getBytes());
        pojo.setMyVarchar("varch");
        pojo.setMyImage("image".getBytes());

        return pojo;
    }


}
