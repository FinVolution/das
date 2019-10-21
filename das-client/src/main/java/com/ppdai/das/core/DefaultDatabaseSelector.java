package com.ppdai.das.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.ppdai.das.core.markdown.MarkdownManager;
import com.ppdai.das.core.status.StatusManager;

public class DefaultDatabaseSelector implements DatabaseSelector, DasComponent {

    @Override
    public void initialize(Map<String, String> settings) throws Exception {
    }

    @Override
	public String select(SelectionContext context) throws DasException {
        String designatedDatasource = context.getDesignatedDatabase();
        HaContext ha = context.getHaContext();
        
        List<DataBase> primary;
        List<DataBase> secondary = null;

        
        if(context.getHints().is(HintEnum.slaveOnly)) {
            primary = context.getSlaves();
        } else if(context.isMasterOnly() || !context.isSelect()) {
		    primary = context.getMasters();
		} else {
		    primary = context.getSlaves();
		    secondary = context.getMasters();
		}

		if(isNullOrEmpty(primary) && isNullOrEmpty(secondary))
			throw new DasException(ErrorCode.NullLogicDbName);
		
		if(designatedDatasource != null){
			if(!StatusManager.containsDataSourceStatus(designatedDatasource))
				throw new DasException(ErrorCode.InvalidDatabaseKeyName, designatedDatasource);
		
			if(MarkdownManager.isMarkdown(designatedDatasource))
				throw new DasException(ErrorCode.MarkdownConnection, designatedDatasource);
			
			if(ha != null && ha.contains(designatedDatasource)) {
				ha.setOver(true);
				throw new DasException(ErrorCode.NoMoreConnectionToFailOver);
			}
			
			if(containsDesignatedDatasource(designatedDatasource, primary))
				return designatedDatasource;

			if(containsDesignatedDatasource(designatedDatasource, secondary))
				return designatedDatasource;
			
			throw new DasException(ErrorCode.InvalidDatabaseKeyName, designatedDatasource);
		}
		
		String dbName = getAvailableDb(ha, primary);
		if(dbName != null)
			return dbName;

		dbName = getAvailableDb(ha, secondary);
		if(dbName != null)
			return dbName;
		
		if(ha != null) {
			ha.setOver(true);
			throw new DasException(ErrorCode.NoMoreConnectionToFailOver);
		}

		StringBuilder sb = new StringBuilder(toDbNames(primary));
		if(isNullOrEmpty(secondary))
			sb.append(", " + toDbNames(secondary));
		
		throw new DasException(ErrorCode.MarkdownConnection, sb.toString());
	}
	
	private String getAvailableDb(HaContext ha, List<DataBase> candidates) throws DasException{
		if(isNullOrEmpty(candidates))
			return null;
		List<String> dbNames = this.selectValidDbNames(candidates);
		if(dbNames.isEmpty())
			return null;
		return this.getRandomRealDbName(ha, dbNames);
	}
	
	private String getRandomRealDbName(HaContext ha, List<String> dbs) throws DasException{
		if(ha == null|| dbs.size() == 1){
			return choseByRandom(dbs);
		}else{
			List<String> dbNames = new ArrayList<String>();
			for (String database : dbs) {
				if(!ha.contains(database))
					dbNames.add(database);
			}
			if(dbNames.isEmpty()){
				return null;
			}else{
				String selected = choseByRandom(dbNames);
				ha.addDB(selected);
				return selected;
			}
		}
	}
	
	private String choseByRandom(List<String> dbs) throws DasException {
		int index = (int)(Math.random() * dbs.size());	
		return dbs.get(index);
	}
	
	private List<String> selectValidDbNames(List<DataBase> dbs){
		List<String> dbNames = new ArrayList<String>();
		if(!this.isNullOrEmpty(dbs)){
			for (DataBase database : dbs) {
				if(MarkdownManager.isMarkdown(database.getConnectionString()))
					continue;

				dbNames.add(database.getConnectionString());
			}
		}
		return dbNames;
	}
	
	private boolean containsDesignatedDatasource(String designatedDatasource, List<DataBase> dbs){
		if(isNullOrEmpty(dbs))
			return false;

		for (DataBase database : dbs)
			if(designatedDatasource.equals(database.getConnectionString()))
				return true;

		return false;
	}
	
	private String toDbNames(List<DataBase> dbs){
		if(this.isNullOrEmpty(dbs)){
			return "";
		}
		List<String> dbNames = new ArrayList<String>();
		for (DataBase database : dbs) {
			dbNames.add(database.getConnectionString());
		}
		return StringUtils.join(dbNames, ",");
	}

	@SuppressWarnings("rawtypes")
	private boolean isNullOrEmpty(List list){
		return list == null || list.isEmpty();
	}
}
