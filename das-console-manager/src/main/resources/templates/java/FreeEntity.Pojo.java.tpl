package ${host.getPackageName()}.entity;

import javax.persistence.Column;
#foreach( $field in ${host.getPojoImports()} )
import ${field};
#end

public class ${host.getPojoClassName()} {

#foreach( $field in ${host.getFields()} )
	@Column(name="${field.getName()}"#if($field.isDataChangeLastTimeField()), insertable=false, updatable=false#end)
	private ${field.getClassDisplayName()} ${field.getCamelCaseUncapitalizedName()};

#end

#foreach( $field in ${host.getFields()} )
	public ${field.getClassDisplayName()} get${field.getCamelCaseCapitalizedName()}() {
		return ${field.getCamelCaseUncapitalizedName()};
	}

#end
}

#*import java.sql.Timestamp;*#
