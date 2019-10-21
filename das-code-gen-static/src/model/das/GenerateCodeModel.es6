import {Model} from 'ea-react-dm-v14'

@Model('GenerateCodeModel')
export default class GenerateCodeModel {
    static rs

    static list = []

    static suggestion = {}

    static searchInfo = {
        totalCount: 0,
        page: 1,
        pageSize: 10,
        sort: 'id',
        data: {//DatabaseSet
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
                name: '',
                width: 5,
                checkbox: true
            },
            {
                name: 'dbset 名称',
                width: 10,
                key: 'name',
                sort: true
            },
            {
                name: 'provider',
                width: 15,
                map: {1: 'MySql', 2: 'SQLServer'},
                key: 'db_type',
                sort: true
            },
            {
                name: 'sharding',
                width: 30,
                key: 'shardingStrategy',
                sort: true
            },
            {
                name: '修改时间',
                width: 15,
                key: 'update_time'
            },
            {
                name: '操作',
                width: 10,
                key: null,
                button: {editor: true, delete: true}
            }
        ]
    }

    static tree = []

    static dbSetInfo = {//DatabaseSet
        id: '',
        name: '',
        db_type: 1,
        shardingStrategy: '',
        groupId: ''
    }

    static member = {
        suggestionMember: {},
        user_id: null,
        group_id: null
    }
}