import {Model} from 'ea-react-dm-v14'

@Model('MemberModel')
export default class MemberModel {
    static rs

    static list = []

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
                name: '工号',
                width: 10,
                key: 'userNo',
                sort: true,
                sortKey: 'user_no'
            },
            {
                name: '组员',
                width: 10,
                key: 'userName',
                sort: true,
                sortKey: 'user_name'
            },
            {
                name: '姓名',
                width: 10,
                key: 'userRealName',
                sort: true,
                sortKey: 'user_real_name'
            },
            {
                name: '电子邮件',
                width: 15,
                key: 'userEmail'
            },
            {
                name: '组员角色',
                width: 10,
                map: {1: '组管理员', 2: '组员'},
                key: 'role'
            },
            {
                name: '修改时间',
                width: 10,
                key: 'update_time',
                sort: true,
                search: false
            },
            {
                name: '操作人',
                width: 10,
                key: 'update_user_name',
                search: false
            },
            {
                name: '操作',
                width: 20,
                key: null,
                button: {editor: true, delete: true}
            }
        ]
    }

    static tree = []

    static member = {
        suggestionMember: {},
        user_id: null,
        group_id: null,
        role: 2
    }
}