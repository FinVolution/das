package com.ppdai.platform.das.codegen.common.codeGen.entity;

public class ExecuteResult {
	private String taskName;
	private boolean successal;
	
	public ExecuteResult(String name){
		this.taskName = name;
	}
	
	public void setSuccessal(boolean success){
		this.successal = success;
	}
	
	public String getTaskName(){
		return this.taskName;
	}
}
