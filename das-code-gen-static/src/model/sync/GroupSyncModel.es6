import {Model} from 'ea-react-dm-v14'

@Model('GroupSyncModel')
export default class GroupSyncModel {
    static rs

    static list = []

    static searchInfo = {
        totalCount: 0,
        page: 1,
        pageSize: 10,
        sort: 'id'
    }

    static states = {
        editeVisible: false,
        checkVisible: false
    }

    static columnInfo = {
        column: [
            {
                name: 'Team 名称',
                width: 20,
                key: 'group_name',
                sort: true
            },
            {
                name: '备注',
                width: 30,
                key: 'group_comment'
            },
            {
                name: '创建时间',
                width: 10,
                key: 'insert_time',
                sort: true
            },
            {
                name: '操作人',
                width: 20,
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

    static group = {
        id: 0,
        group_name: '',
        group_comment: ''
    }

    static tree = []
}