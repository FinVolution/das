package com.ppdai.das.util;


import com.ppdai.das.client.ColumnDefinition;
import com.ppdai.das.client.TableDefinition;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.JDBCType;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Objects;


@Table(name="testtable")
public class TesttableSQLServer {

    public static final TesttableSQLServerDefinition TESTTABLESQLSERVER = new TesttableSQLServerDefinition();

    public static class TesttableSQLServerDefinition extends TableDefinition {
        public final ColumnDefinition MyID;
        public final ColumnDefinition MyBigint;
        public final ColumnDefinition MyNumeric;
        public final ColumnDefinition MyBit;
        public final ColumnDefinition MySmallint;
        public final ColumnDefinition MyDecimal;
        public final ColumnDefinition MySmallmoney;
        public final ColumnDefinition MyTinyint;
        public final ColumnDefinition MyMoney;
        public final ColumnDefinition MyFloat;
        public final ColumnDefinition MyReal;
        public final ColumnDefinition MyDate;
        //public final ColumnDefinition MyDatetimeoffset;
        public final ColumnDefinition MyDatetime2;
        public final ColumnDefinition MySmalldatetime;
        public final ColumnDefinition MyDatetime;
        public final ColumnDefinition MyTime;
        public final ColumnDefinition MyChar;
        public final ColumnDefinition MyVarchar;
        public final ColumnDefinition MyText;
        public final ColumnDefinition MyBinary;
        public final ColumnDefinition MyVarbinary;
        public final ColumnDefinition MyImage;
        public TesttableSQLServerDefinition as(String alias) {return _as(alias);}
        public TesttableSQLServerDefinition inShard(String shardId) {return _inShard(shardId);}
        public TesttableSQLServerDefinition shardBy(String shardValue) {return _shardBy(shardValue);}

        public TesttableSQLServerDefinition() {
            super("testtable");
            setColumnDefinitions(
                    MyID = column("MyID", JDBCType.INTEGER),
                    MyBigint = column("MyBigint", JDBCType.BIGINT),
                    MyNumeric = column("MyNumeric", JDBCType.NUMERIC),
                    MyBit = column("MyBit", JDBCType.BIT),
                    MySmallint = column("MySmallint", JDBCType.SMALLINT),
                    MyDecimal = column("MyDecimal", JDBCType.DECIMAL),
                    MySmallmoney = column("MySmallmoney", JDBCType.DECIMAL),
                    MyTinyint = column("MyTinyint", JDBCType.TINYINT),
                    MyMoney = column("MyMoney", JDBCType.DECIMAL),
                    MyFloat = column("MyFloat", JDBCType.DOUBLE),
                    MyReal = column("MyReal", JDBCType.REAL),
                    MyDate = column("MyDate", JDBCType.DATE),
                    //MyDatetimeoffset = column("MyDatetimeoffset", JDBCTypemicrosoft.sql..DATETIMEOFFSET),
                    MyDatetime2 = column("MyDatetime2", JDBCType.TIMESTAMP),
                    MySmalldatetime = column("MySmalldatetime", JDBCType.TIMESTAMP),
                    MyDatetime = column("MyDatetime", JDBCType.TIMESTAMP),
                    MyTime = column("MyTime", JDBCType.TIME),
                    MyChar = column("MyChar", JDBCType.CHAR),
                    MyVarchar = column("MyVarchar", JDBCType.VARCHAR),
                    MyText = column("MyText", JDBCType.LONGVARCHAR),
                    MyBinary = column("MyBinary", JDBCType.BINARY),
                    MyVarbinary = column("MyVarbinary", JDBCType.VARBINARY),
                    MyImage = column("MyImage", JDBCType.LONGVARBINARY)
			);
        }
    }


    @Id
    @Column(name="MyID")
    private Integer myID;

    @Column(name="MyBigint")
    private Long myBigint;

    @Column(name="MyNumeric")
    private BigDecimal myNumeric;

    @Column(name="MyBit")
    private Boolean myBit;

    @Column(name="MySmallint")
    private Short mySmallint;

    @Column(name="MyDecimal")
    private BigDecimal myDecimal;

    @Column(name="MySmallmoney")
    private BigDecimal mySmallmoney;

    @Column(name="MyTinyint")
    private Short myTinyint;

    @Column(name="MyMoney")
    private BigDecimal myMoney;

    @Column(name="MyFloat")
    private Double myFloat;

    @Column(name="MyReal")
    private Float myReal;

    @Column(name="MyDate")
    private Date myDate;

/*    @Column(name="MyDatetimeoffset")
    private DateTimeOffset myDatetimeoffset;*/

    @Column(name="MyDatetime2")
    private Timestamp myDatetime2;

    @Column(name="MySmalldatetime")
    private Timestamp mySmalldatetime;

    @Column(name="MyDatetime")
    private Timestamp myDatetime;

    @Column(name="MyTime")
    private Time myTime;

    @Column(name="MyChar")
    private String myChar;

    @Column(name="MyVarchar")
    private String myVarchar;

    @Column(name="MyText")
    private String myText;

    @Column(name="MyBinary")
    private byte[] myBinary;

    @Column(name="MyVarbinary")
    private byte[] myVarbinary;

    @Column(name="MyImage")
    private byte[] myImage;

    public Integer getMyID() {
        return myID;
    }

    public void setMyID(Integer myID) {
        this.myID = myID;
    }

    public Long getMyBigint() {
        return myBigint;
    }

    public void setMyBigint(Long myBigint) {
        this.myBigint = myBigint;
    }

    public BigDecimal getMyNumeric() {
        return myNumeric;
    }

    public void setMyNumeric(BigDecimal myNumeric) {
        this.myNumeric = myNumeric;
    }

    public Boolean getMyBit() {
        return myBit;
    }

    public void setMyBit(Boolean myBit) {
        this.myBit = myBit;
    }

    public Short getMySmallint() {
        return mySmallint;
    }

    public void setMySmallint(Short mySmallint) {
        this.mySmallint = mySmallint;
    }

    public BigDecimal getMyDecimal() {
        return myDecimal;
    }

    public void setMyDecimal(BigDecimal myDecimal) {
        this.myDecimal = myDecimal;
    }

    public BigDecimal getMySmallmoney() {
        return mySmallmoney;
    }

    public void setMySmallmoney(BigDecimal mySmallmoney) {
        this.mySmallmoney = mySmallmoney;
    }

    public Short getMyTinyint() {
        return myTinyint;
    }

    public void setMyTinyint(Short myTinyint) {
        this.myTinyint = myTinyint;
    }

    public BigDecimal getMyMoney() {
        return myMoney;
    }

    public void setMyMoney(BigDecimal myMoney) {
        this.myMoney = myMoney;
    }

    public Double getMyFloat() {
        return myFloat;
    }

    public void setMyFloat(Double myFloat) {
        this.myFloat = myFloat;
    }

    public Float getMyReal() {
        return myReal;
    }

    public void setMyReal(Float myReal) {
        this.myReal = myReal;
    }

    public Date getMyDate() {
        return myDate;
    }

    public void setMyDate(Date myDate) {
        this.myDate = myDate;
    }

 /*   public DateTimeOffset getMyDatetimeoffset() {
        return myDatetimeoffset;
    }

    public void setMyDatetimeoffset(DateTimeOffset myDatetimeoffset) {
        this.myDatetimeoffset = myDatetimeoffset;
    }*/

    public Timestamp getMyDatetime2() {
        return myDatetime2;
    }

    public void setMyDatetime2(Timestamp myDatetime2) {
        this.myDatetime2 = myDatetime2;
    }

    public Timestamp getMySmalldatetime() {
        return mySmalldatetime;
    }

    public void setMySmalldatetime(Timestamp mySmalldatetime) {
        this.mySmalldatetime = mySmalldatetime;
    }

    public Timestamp getMyDatetime() {
        return myDatetime;
    }

    public void setMyDatetime(Timestamp myDatetime) {
        this.myDatetime = myDatetime;
    }

    public Time getMyTime() {
        return myTime;
    }

    public void setMyTime(Time myTime) {
        this.myTime = myTime;
    }

    public String getMyChar() {
        return myChar;
    }

    public void setMyChar(String myChar) {
        this.myChar = myChar;
    }

    public String getMyVarchar() {
        return myVarchar;
    }

    public void setMyVarchar(String myVarchar) {
        this.myVarchar = myVarchar;
    }

    public String getMyText() {
        return myText;
    }

    public void setMyText(String myText) {
        this.myText = myText;
    }

    public byte[] getMyBinary() {
        return myBinary;
    }

    public void setMyBinary(byte[] myBinary) {
        this.myBinary = myBinary;
    }

    public byte[] getMyVarbinary() {
        return myVarbinary;
    }

    public void setMyVarbinary(byte[] myVarbinary) {
        this.myVarbinary = myVarbinary;
    }

    public byte[] getMyImage() {
        return myImage;
    }

    public void setMyImage(byte[] myImage) {
        this.myImage = myImage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TesttableSQLServer that = (TesttableSQLServer) o;
        return Objects.equals(myID, that.myID) &&
                Objects.equals(myBigint, that.myBigint) &&
                Objects.equals(myNumeric, that.myNumeric) &&
                Objects.equals(myBit, that.myBit) &&
                Objects.equals(mySmallint, that.mySmallint) &&
                Objects.equals(myDecimal, that.myDecimal) &&
                Objects.equals(mySmallmoney, that.mySmallmoney) &&
                Objects.equals(myTinyint, that.myTinyint) &&
                Objects.equals(myMoney, that.myMoney) &&
                Objects.equals(myFloat, that.myFloat) &&
                Objects.equals(myReal, that.myReal) &&
                Objects.equals(myDate, that.myDate) &&
                Objects.equals(myDatetime2, that.myDatetime2) &&
                Objects.equals(mySmalldatetime, that.mySmalldatetime) &&
                Objects.equals(myDatetime, that.myDatetime) &&
                Objects.equals(myTime, that.myTime) &&
                Objects.equals(myChar, that.myChar) &&
                Objects.equals(myVarchar, that.myVarchar) &&
                Objects.equals(myText, that.myText) &&
                Arrays.equals(myBinary, that.myBinary) &&
                Arrays.equals(myVarbinary, that.myVarbinary) &&
                Arrays.equals(myImage, that.myImage);
    }

    @Override
    public int hashCode() {

        int result = Objects.hash(myID, myBigint, myNumeric, myBit, mySmallint, myDecimal, mySmallmoney, myTinyint, myMoney, myFloat, myReal, myDate, myDatetime2, mySmalldatetime, myDatetime, myTime, myChar, myVarchar, myText);
        result = 31 * result + Arrays.hashCode(myBinary);
        result = 31 * result + Arrays.hashCode(myVarbinary);
        result = 31 * result + Arrays.hashCode(myImage);
        return result;
    }
}
