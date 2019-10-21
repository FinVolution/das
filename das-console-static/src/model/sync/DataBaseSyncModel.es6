import {Model} from 'ea-react-dm-v14'

@Model('DataBaseSyncModel')
export default class DataBaseSyncModel {
    static rs

    static list = []

    static suggestion = {}

    static searchInfo = {
        totalCount: 0,
        page: 1,
        pageSize: 10,
        sort: 'id',
        data: {
            id: null
        }
    }

    static states = {
        editeVisible: false,
        checkVisible: false
    }

    static columnInfo = {
        column: [
            {
                name: '数据库标识符',
                width: 15,
                key: 'dbname',
                sort: true,
                sortKey: 'db_name'
            },
            {
                name: '类型',
                width: 10,
                map: {1: 'MySql', 2: 'SQLServer'},
                key: 'db_type',
                sort: true
            },
            {
                name: '所属组',
                width: 10,
                key: 'group_name',
                sort: true,
                sortKey: 'dal_group_id'
            },
            {
                name: '数据库名',
                width: 15,
                key: 'db_catalog'
            },
            {
                name: '备注',
                width: 20,
                key: 'comment',
                popover: {title: '备注', maximum: 30}
            },
            {
                name: '更新时间',
                width: 10,
                key: 'update_time',
                sort: true
            },
            {
                name: '操作人',
                width: 10,
                key: 'userRealName'
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

    static dbinfo = {
        'id': '',
        'comment': '',
        'dal_group_id': ''
    }

    static member = {
        suggestionMember: {},
        user_id: null,
        group_id: null
    }
}