package com.ppdai.das.client;

import java.sql.Timestamp;

import javax.persistence.Column;

public class FreeEntityPojo {
	
	@Column(name="PeopleID")
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
	private Timestamp dataChange_LastTime;

	public Integer getPeopleID() {
		return peopleID;
	}

	public String getName() {
		return name;
	}

	public Integer getCityID() {
		return cityID;
	}

	public Integer getProvinceID() {
		return provinceID;
	}

	public Integer getCountryID() {
		return countryID;
	}

	public Timestamp getDataChange_LastTime() {
		return dataChange_LastTime;
	}
}
