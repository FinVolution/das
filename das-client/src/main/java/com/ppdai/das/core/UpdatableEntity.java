package com.ppdai.das.core;

import java.util.HashSet;
import java.util.Set;

public class UpdatableEntity {
	private Set<String> updatedColumns = new HashSet<>();
	public UpdatableEntity() {
		
	}
	
	public void update(String column) {
		updatedColumns.add(column);
	}
	
	public void clear(String column) {
		updatedColumns.remove(column);
	}
	
	public boolean isUpdated(String column) {
		return updatedColumns.contains(column);
	}
	
	public void reset() {
		updatedColumns.clear();
	}
	
	public Set<String> getUpdatedColumns() {
		return updatedColumns;
	}
}
