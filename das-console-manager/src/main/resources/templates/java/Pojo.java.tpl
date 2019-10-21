package ${host.getPackageName()}.entity;

import java.sql.JDBCType;
#foreach( $field in ${host.getPojoImports()} )
import ${field};
#end
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import com.ppdai.das.client.ColumnDefinition;
import com.ppdai.das.client.TableDefinition;

/**
 * create by das-console
 * 请勿修改此文件
 */
#*@Table(name="$!{host.getCustomTableame()}")*#
@Table
public final class ${host.getPojoClassName()} {

    public static final ${host.getPojoClassName()}Definition ${host.getPojoClassName().toUpperCase()} = new ${host.getPojoClassName()}Definition();

    public static class ${host.getPojoClassName()}Definition extends TableDefinition {
        #foreach( $field in ${host.getFields()} )
    public final ColumnDefinition ${field.getCamelCaseUncapitalizedName()};
        #end

        public ${host.getPojoClassName()}Definition as(String alias) {
            return _as(alias);
        }
        public ${host.getPojoClassName()}Definition inShard(String shardId) {
            return _inShard(shardId);
        }

        public ${host.getPojoClassName()}Definition shardBy(String shardValue) {
            return _shardBy(shardValue);
        }

        public ${host.getPojoClassName()}Definition() {
            super("$!{host.getCustomTableame()}");
		#foreach( $field in ${host.getFields()} )
			${field.getCamelCaseUncapitalizedName()} = column("${field.getName()}", ${field.getJDBCTypeDisplay()});
		#end
            setColumnDefinitions(
            #foreach( $field in ${host.getColumnDefinitions()} )
            ${field}
            #end);
        }
    }

#foreach( $field in ${host.getFields()} )

#if($field.getComment() != "")
    /** $field.getComment() **/
#end
#if(${field.isPrimary()})
	@Id
#end
	@Column(name = "${field.getName()}"#if($field.isDataChangeLastTimeField()), insertable=false, updatable=false#end)
#if(${field.isIdentity()})
	@GeneratedValue(strategy = GenerationType.AUTO)
#end
	private ${field.getClassDisplayName()} ${field.getCamelCaseUncapitalizedName()};
#end

#foreach( $field in ${host.getFields()} )
	public ${field.getClassDisplayName()} get${field.getCamelCaseCapitalizedName()}() {
		return ${field.getCamelCaseUncapitalizedName()};
	}

	public void set${field.getCamelCaseCapitalizedName()}(${field.getClassDisplayName()} a${field.getCamelCaseCapitalizedName()}) {
		this.${field.getCamelCaseUncapitalizedName()} = a${field.getCamelCaseCapitalizedName()};
	}

#end
}