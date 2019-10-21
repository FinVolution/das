
import com.ppdai.das.client.SqlBuilder;
import com.ppdai.das.client.Parameter;
import com.ppdai.das.client.DasClientFactory;
import com.ppdai.das.client.DasClient;
import java.sql.SQLException;
#foreach($importResultMap in $importResultMaps)
import $importResultMap;
#end

/** Mapper XML中每个SQL节点会转换成 $className 中的一个方法
#foreach($classComment in $classComments)
$classComment;
#end

此代码需要手工调试检查，以确保正确
**/
public class $className {
#foreach($statement in $statements)
    #if($statement.isFail())
        /**
            无法生成的代码为id = "${statement.getIdAsName()}"XML片段，
            建议修改XML后重试
         **/
    #else
        /** 以下代码为id = "${statement.getIdAsName()}"生成的代码片段
            $statement.getComments()
        **/
        public ${statement.getResultType()} ${statement.getIdAsName()} (#foreach($en in $statement.getParams().entrySet())${en.getValue()} ${en.getKey()}#if( $foreach.hasNext), #end#end) throws SQLException {
        SqlBuilder sqlBuilder = new SqlBuilder();
            ${statement.getMethodBody()}
            DasClient dao = DasClientFactory.getClient(logicDBName);
            return dao.${statement.getMethodType()}(sqlBuilder);
        }
    #end
#end
}
#*
${resultType} ${id}(#foreach($en in $inMap.entrySet())${en.getValue()} ${en.getKey()}#if( $foreach.hasNext),#end#end){
    SqlBuilder sqlBuilder = SqlBuilder.appendTemplate("${sql}",
#foreach($en in $inMap.entrySet())
Parameter.${en.getValue()}Of("", ${en.getKey()})#if( $foreach.hasNext ),#end
#end
));
    DasClient dao = DasClientFactory.getClient(logicDBName);
    return dao.${method}(sqlBuilder);
}
*#
