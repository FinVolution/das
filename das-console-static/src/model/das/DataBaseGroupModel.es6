import {Model} from 'ea-react-dm-v14'

@Model('DataBaseGroupModel')
export default class DataBaseGroupModel {
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
                name: '物理库标识符',
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
                name: '物理库名',
                width: 15,
                key: 'db_catalog',
                sort: true,
                sortKey: 'db_catalog'
            },
            {
                name: '所属组',
                width: 10,
                key: 'group_name'
            },
            {
                name: '备注',
                width: 10,
                key: 'comment',
                popover: {title: '备注', maximum: 30}
            },
            {
                name: '更新时间',
                width: 10,
                key: 'update_time',
                sort: true,
                search: false
            },
            {
                name: '操作人',
                width: 10,
                key: 'userRealName',
                search: false
            },
            {
                name: '操作',
                width: 10,
                key: null,
                button: {delete: true}
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