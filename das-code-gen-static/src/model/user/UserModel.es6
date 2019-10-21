import {Model} from 'ea-react-dm-v14'

@Model('UserModel')
export default class UserModel {
    static menus = []

    static rs

    static list = []

    static searchInfo = {
        totalCount: 0,
        page: 1,
        pageSize: 10,
        sort: 'id',
        data: {
            user_no: ''

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
                name: '域账号',
                width: 15,
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
                name: '邮箱',
                width: 20,
                key: 'userEmail'
            },
            {
                name: '操作人',
                width: 10,
                key: 'updateUserName',
                search: false
            },
            {
                name: '组名',
                width: 15,
                key: 'group_name',
                search: false
            },
            {
                name: '操作',
                width: 10,
                key: null,
                button: {
                    add: {display: true, key: 'add'},
                    editor: {display: true}
                }
            }
        ]
    }

    static loginUser = {
        userNo: '',
        userName: '',
        userEmail: '',
        password: ''
    }

}