<das name="$host.getAppId()">
	<databaseSets>
#foreach($databaseSet in $host.getDatabaseSets())
#if($databaseSet.hasShardingStrategy())
		<databaseSet name="$databaseSet.getName()" provider="$databaseSet.getProvider()"
             shardingStrategy="$databaseSet.getShardingStrategy()">
#else
		<databaseSet name="$databaseSet.getName()" provider="$databaseSet.getProvider()">
#end
#foreach($entry in $databaseSet.getDatabaseSetEntryList())
            <add name="$entry.getName()" connectionString="$entry.getDbName()" databaseType="$entry.getDatabaseTypeName()" sharding="$entry.getSharding()"/>
#end
		</databaseSet>
#end
	</databaseSets>
</das>