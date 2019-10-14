package com.ppdai.das.client;

import java.sql.JDBCType;
//import java.sql.Timestamp;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.ppdai.das.client.ColumnDefinition;
import com.ppdai.das.client.TableDefinition;

@Table
public class Person {
    public static final PersonDefinition PERSON = new PersonDefinition();
    
	public static class PersonDefinition extends TableDefinition {
	    public final ColumnDefinition PeopleID;
	    public final ColumnDefinition Name;
	    public final ColumnDefinition CityID;
	    public final ColumnDefinition ProvinceID;
	    public final ColumnDefinition CountryID;
	    public final ColumnDefinition DataChange_LastTime;
        public PersonDefinition as(String alias) {return _as(alias);}
        public PersonDefinition inShard(String shardId) {return _inShard(shardId);}
        public PersonDefinition shardBy(String shardValue) {return _shardBy(shardValue);}
        public PersonDefinition() {
            super("person");
            PeopleID = column("PeopleID", JDBCType.INTEGER);
            Name = column("Name", JDBCType.VARCHAR);
            CityID = column("CityID", JDBCType.INTEGER);
            ProvinceID = column("ProvinceID", JDBCType.INTEGER);
            CountryID = column("CountryID", JDBCType.INTEGER);
            DataChange_LastTime = column("DataChange_LastTime", JDBCType.TIMESTAMP);
            setColumnDefinitions(PeopleID, Name, CityID, ProvinceID, CountryID, DataChange_LastTime);
        }
	}

    @Id
	@Column(name="PeopleID")
    @GeneratedValue(strategy = GenerationType.AUTO)
	private Integer peopleID;
	
	@Column(name="Name")
	private String name;
	
	@Column(name="CityID")
	private Integer cityID;
	
	@Column(name="ProvinceID")
	private Integer provinceID;
	
	@Column(name="CountryID")
	private Integer countryID;
	
	@Column(name="DataChange_LastTime")
	private Date dataChange_LastTime;

	public Integer getPeopleID() {
		return peopleID;
	}

	public void setPeopleID(Integer peopleID) {
		this.peopleID = peopleID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getCityID() {
		return cityID;
	}

	public void setCityID(Integer cityID) {
		this.cityID = cityID;
	}

	public Integer getProvinceID() {
		return provinceID;
	}

	public void setProvinceID(Integer provinceID) {
		this.provinceID = provinceID;
	}

	public Integer getCountryID() {
		return countryID;
	}

	public void setCountryID(Integer countryID) {
		this.countryID = countryID;
	}

	public Date getDataChange_LastTime() {
		return dataChange_LastTime;
	}

	public void setDataChange_LastTime(Date dataChange_LastTime) {
		this.dataChange_LastTime = dataChange_LastTime;
	}
}
