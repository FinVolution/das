import {Model} from 'ea-react-dm-v14'
import {UserEnv} from '../../view/components/utils/util/Index'

@Model('DatabaseSetEntryModel')
export default class DatabaseSetEntryModel {
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
                name: '逻辑库映射',
                width: 15,
                key: 'name',
                sort: true
            },
            {
                name: '类型',
                width: 8,
                map: {1: 'Master', 2: 'Slave'},
                key: 'databaseType',
                sort: true,
                sortKey: 'database_type'
            },
            {
                name: 'sharding',
                width: 8,
                key: 'sharding',
                sort: true
            },
            {
                name: '物理库标识符',
                width: 15,
                key: 'dbName'
            },
            {
                name: '物理库名',
                width: 15,
                key: 'db_catalog'
            },
            {
                name: '修改时间',
                width: 15,
                key: 'update_time',
                sort: true,
                search: false
            },
            {
                name: '操作人',
                width: 8,
                key: 'userRealName',
                sort: true,
                sortKey: 'user_real_name',
                search: false
            },
            {
                name: '操作',
                width: 15,
                key: null,
                button: {
                    add: {display: true, key: 'add'},
                    editor: {isAdmin: true},
                    delete: {isAdmin: true},
                    sync: {isAdmin: true, title: '若' + UserEnv.getConfigCenterName() + '操作失败，可使用此功能同步数据'},
                    check: {title: '查看' + UserEnv.getConfigCenterName() + '数据的正确性'}
                }
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

    static dbSetEntryList = [
        /*{
            'name': 'dal-1',
            'databaseType': 'Master ',
            'sharding': '21212',
            'db_Id': 1,
            'dbset_id': 14,
            'groupId': 1
        },
        {
            'name': 'dal-1',
            'databaseType': 'Master ',
            'sharding': '21212',
            'db_Id': 1,
            'dbset_id': 14,
            'groupId': 1
        },
        {
            'name': 'dal-1',
            'databaseType': 'Master ',
            'sharding': '21212',
            'db_Id': 1,
            'dbset_id': 14,
            'groupId': 1
        },*/
    ]
}