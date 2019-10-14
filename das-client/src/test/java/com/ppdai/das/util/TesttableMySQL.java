package com.ppdai.das.util;


import com.ppdai.das.client.ColumnDefinition;
import com.ppdai.das.client.TableDefinition;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.JDBCType;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Objects;

@Table(name = "testtable")
public class TesttableMySQL {

    public static final TesttableMySQLDefinition TESTTABLEMYSQL = new TesttableMySQLDefinition();

    public static class TesttableMySQLDefinition extends TableDefinition {
        public final ColumnDefinition MyID;
        public final ColumnDefinition MyBit;
        public final ColumnDefinition MyTinyint;
        public final ColumnDefinition MySmallint;
        public final ColumnDefinition MyMediumint;
        public final ColumnDefinition MyBigint;
        public final ColumnDefinition MyDecimal;
        public final ColumnDefinition MyFloat;
        public final ColumnDefinition MyDouble;
        public final ColumnDefinition MyBool;
        public final ColumnDefinition MyDate;
        public final ColumnDefinition MyDatetime;
        public final ColumnDefinition MyTimestamp;
        public final ColumnDefinition MyTime;
        public final ColumnDefinition MyYear;
        public final ColumnDefinition MyVarchar;
        public final ColumnDefinition MyChar;
        public final ColumnDefinition MyText;
        public final ColumnDefinition MyBinary;
        public final ColumnDefinition MyVarbinary;
        public final ColumnDefinition MyBlob;

        public TesttableMySQLDefinition as(String alias) {
            return _as(alias);
        }

        public TesttableMySQLDefinition inShard(String shardId) {
            return _inShard(shardId);
        }

        public TesttableMySQLDefinition shardBy(String shardValue) {
            return _shardBy(shardValue);
        }

        public TesttableMySQLDefinition() {
            super("testtable");
            setColumnDefinitions(
                    MyID = column("MyID", JDBCType.INTEGER),
                    MyBit = column("MyBit", JDBCType.BIT),
                    MyTinyint = column("MyTinyint", JDBCType.TINYINT),
                    MySmallint = column("MySmallint", JDBCType.SMALLINT),
                    MyMediumint = column("MyMediumint", JDBCType.INTEGER),
                    MyBigint = column("MyBigint", JDBCType.BIGINT),
                    MyDecimal = column("MyDecimal", JDBCType.DECIMAL),
                    MyFloat = column("MyFloat", JDBCType.REAL),
                    MyDouble = column("MyDouble", JDBCType.DOUBLE),
                    MyBool = column("MyBool", JDBCType.BIT),
                    MyDate = column("MyDate", JDBCType.DATE),
                    MyDatetime = column("MyDatetime", JDBCType.TIMESTAMP),
                    MyTimestamp = column("MyTimestamp", JDBCType.TIMESTAMP),
                    MyTime = column("MyTime", JDBCType.TIME),
                    MyYear = column("MyYear", JDBCType.DATE),
                    MyVarchar = column("MyVarchar", JDBCType.VARCHAR),
                    MyChar = column("MyChar", JDBCType.CHAR),
                    MyText = column("MyText", JDBCType.LONGVARCHAR),
                    MyBinary = column("MyBinary", JDBCType.BINARY),
                    MyVarbinary = column("MyVarbinary", JDBCType.VARBINARY),
                    MyBlob = column("MyBlob", JDBCType.LONGVARBINARY)
            );
        }
    }


    @Id
    @Column(name = "MyID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer myID;

    @Column(name = "MyBit")
    private Boolean myBit;

    @Column(name = "MyTinyint")
    private Integer myTinyint;

    @Column(name = "MySmallint")
    private Integer mySmallint;

    @Column(name = "MyMediumint")
    private Integer myMediumint;

    @Column(name = "MyBigint")
    private Long myBigint;

    @Column(name = "MyDecimal")
    private BigDecimal myDecimal;

    @Column(name = "MyFloat")
    private Float myFloat;

    @Column(name = "MyDouble")
    private Double myDouble;

    @Column(name = "MyBool")
    private Boolean myBool;

    @Column(name = "MyDate")
    private Date myDate;

    @Column(name = "MyDatetime")
    private Timestamp myDatetime;

    @Column(name = "MyTimestamp")
    private Timestamp myTimestamp;

    @Column(name = "MyTime")
    private Time myTime;

    @Column(name = "MyYear")
    private Date myYear;

    @Column(name = "MyVarchar")
    private String myVarchar;

    @Column(name = "MyChar")
    private String myChar;

    @Column(name = "MyText")
    private String myText;

    @Column(name = "MyBinary")
    private byte[] myBinary;

    @Column(name = "MyVarbinary")
    private byte[] myVarbinary;

    @Column(name = "MyBlob")
    private byte[] myBlob;

    public Integer getMyID() {
        return myID;
    }

    public void setMyID(Integer myID) {
        this.myID = myID;
    }

    public Boolean getMyBit() {
        return myBit;
    }

    public void setMyBit(Boolean myBit) {
        this.myBit = myBit;
    }

    public Integer getMyTinyint() {
        return myTinyint;
    }

    public void setMyTinyint(Integer myTinyint) {
        this.myTinyint = myTinyint;
    }

    public Integer getMySmallint() {
        return mySmallint;
    }

    public void setMySmallint(Integer mySmallint) {
        this.mySmallint = mySmallint;
    }

    public Integer getMyMediumint() {
        return myMediumint;
    }

    public void setMyMediumint(Integer myMediumint) {
        this.myMediumint = myMediumint;
    }

    public Long getMyBigint() {
        return myBigint;
    }

    public void setMyBigint(Long myBigint) {
        this.myBigint = myBigint;
    }

    public BigDecimal getMyDecimal() {
        return myDecimal;
    }

    public void setMyDecimal(BigDecimal myDecimal) {
        this.myDecimal = myDecimal;
    }

    public Float getMyFloat() {
        return myFloat;
    }

    public void setMyFloat(Float myFloat) {
        this.myFloat = myFloat;
    }

    public Double getMyDouble() {
        return myDouble;
    }

    public void setMyDouble(Double myDouble) {
        this.myDouble = myDouble;
    }

    public Boolean getMyBool() {
        return myBool;
    }

    public void setMyBool(Boolean myBool) {
        this.myBool = myBool;
    }

    public Date getMyDate() {
        return myDate;
    }

    public void setMyDate(Date myDate) {
        this.myDate = myDate;
    }

    public Timestamp getMyDatetime() {
        return myDatetime;
    }

    public void setMyDatetime(Timestamp myDatetime) {
        this.myDatetime = myDatetime;
    }

    public Timestamp getMyTimestamp() {
        return myTimestamp;
    }

    public void setMyTimestamp(Timestamp myTimestamp) {
        this.myTimestamp = myTimestamp;
    }

    public Time getMyTime() {
        return myTime;
    }

    public void setMyTime(Time myTime) {
        this.myTime = myTime;
    }

    public Date getMyYear() {
        return myYear;
    }

    public void setMyYear(Date myYear) {
        this.myYear = myYear;
    }

    public String getMyVarchar() {
        return myVarchar;
    }

    public void setMyVarchar(String myVarchar) {
        this.myVarchar = myVarchar;
    }

    public String getMyChar() {
        return myChar;
    }

    public void setMyChar(String myChar) {
        this.myChar = myChar;
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

    public byte[] getMyBlob() {
        return myBlob;
    }

    public void setMyBlob(byte[] myBlob) {
        this.myBlob = myBlob;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TesttableMySQL that = (TesttableMySQL) o;
        return Objects.equals(myID, that.myID) &&
                Objects.equals(myBit, that.myBit) &&
                Objects.equals(myTinyint, that.myTinyint) &&
                Objects.equals(mySmallint, that.mySmallint) &&
                Objects.equals(myMediumint, that.myMediumint) &&
                Objects.equals(myBigint, that.myBigint) &&
                Objects.equals(myDecimal, that.myDecimal) &&
                Objects.equals(myFloat, that.myFloat) &&
                Objects.equals(myDouble, that.myDouble) &&
                Objects.equals(myBool, that.myBool) &&
                Objects.equals(myDate, that.myDate) &&
                Objects.equals(myDatetime, that.myDatetime) &&
                Objects.equals(myTimestamp, that.myTimestamp) &&
                Objects.equals(myTime, that.myTime) &&
                Objects.equals(myYear, that.myYear) &&
                Objects.equals(myVarchar, that.myVarchar) &&
                Objects.equals(myChar, that.myChar) &&
                Objects.equals(myText, that.myText) &&
                Arrays.equals(myBinary, that.myBinary) &&
                Arrays.equals(myVarbinary, that.myVarbinary) &&
                Arrays.equals(myBlob, that.myBlob);
    }

    @Override
    public int hashCode() {

        int result = Objects.hash(myID, myBit, myTinyint, mySmallint, myMediumint, myBigint, myDecimal, myFloat, myDouble, myBool, myDate, myDatetime, myTimestamp, myTime, myYear, myVarchar, myChar, myText);
        result = 31 * result + Arrays.hashCode(myBinary);
        result = 31 * result + Arrays.hashCode(myVarbinary);
        result = 31 * result + Arrays.hashCode(myBlob);
        return result;
    }
}
