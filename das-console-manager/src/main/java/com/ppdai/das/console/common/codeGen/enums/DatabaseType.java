package com.ppdai.das.console.common.codeGen.enums;

public enum DatabaseType {
	
	MySQL("Arch.DataSaec.MySqlProvider"),
	SQLServer("System.Sql.SqlClient");
	
	private String value;
	
	DatabaseType(String value){
		this.value = value;
	}
	
	public String getValue(){
		return this.value;
	}

}
