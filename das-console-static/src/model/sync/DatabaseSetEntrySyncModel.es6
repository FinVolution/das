import {Model} from 'ea-react-dm-v14'

@Model('DatabaseSetEntrySyncModel')
export default class DatabaseSetEntrySyncModel {
    static rs

    static list = []

    static suggestion = {}

    static searchInfo = {
        totalCount: 0,
        page: 1,
        pageSize: 10,
        sort: 'id',
        data: {//DatabaseSetEntry
            dbset_id: null
        }
    }

    static states = {
        editeVisible: false,
        checkVisible: false
    }

    static columnInfo = {
        column: [
            {
                name: 'dbset Entry',
                width: 20,
                key: 'name',
                sort: true
            },
            {
                name: '类型',
                width: 10,
                map: {1: 'Master', 2: 'Slave'},
                key: 'databaseType',
                sort: true,
                sortKey: 'database_type'
            },
            {
                name: 'sharding',
                width: 15,
                key: 'sharding',
                sort: true
            },
            {
                name: '物理库名',
                width: 20,
                key: 'db_catalog'
            },
            {
                name: '修改时间',
                width: 15,
                key: 'update_time',
                sort: true
            },
            {
                name: '操作人',
                width: 10,
                key: 'userRealName',
                sort: true,
                sortKey: 'user_real_name'
            },
            {
                name: '操作',
                width: 10,
                key: null,
                button: {sync: true, syncTitle: '同步数据到当前环境，可重复操作'}
            }
        ]
    }

    static tree = []

    static dbSetEntryInfo = {//DatabaseSetEntry
        name: '',
        databaseType: 1,
        sharding: '',
        db_Id: 0,
        dbset_id: 0,
        groupId: 0
    }

    static member = {
        suggestionMember: {},
        user_id: null,
        group_id: null
    }
}