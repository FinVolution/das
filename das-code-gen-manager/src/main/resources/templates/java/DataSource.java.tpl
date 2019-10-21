<Datasources>
#foreach($resource in $host.getDbs())
	<Datasource name="${resource.getDbname()}"
              userName="${resource.getDb_user()}"
              password="${resource.getDb_password()}"
              connectionUrl="${resource.getConnectionUrl()}"
              driverClassName="${resource.getDriverClassName()}"
              testWhileIdle="false"
              testOnBorrow="false"
              testOnReturn="false"
              validationQuery="SELECT 1"
              validationInterval="30000"
              timeBetweenEvictionRunsMillis="5000"
              maxActive="100"
              minIdle="0"
              maxWait="10000"
              maxAge="30000"
              initialSize="10"
              removeAbandonedTimeout="60"
              removeAbandoned="true"
              logAbandoned="true"
              minEvictableIdleTimeMillis="30000"
#if (${resource.isOptionAppend()})
              connectionProperties="sendTimeAsDateTime=false;sendStringParametersAsUnicode=false"/>
#else
              connectionProperties="rewriteBatchedStatements=true;allowMultiQueries=true"/>
#end
#end			  
</Datasources>
