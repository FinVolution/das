import {Model} from 'ea-react-dm-v14'
import {UserEnv} from '../../view/components/utils/util/Index'

@Model('GroupModel')
export default class GroupModel {
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
                width: 10,
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
                sort: true,
                search: false
            },
            {
                name: '操作人',
                width: 20,
                key: 'userRealName',
                sort: true,
                sortKey: 'user_real_name',
                search: false
            },

            {
                name: '操作',
                width: 20,
                key: null,
                button: {editor: true, delete: true, sync: true, syncTitle: '若' + UserEnv.getConfigCenterName() + '操作失败，可使用此功能同步数据到' + UserEnv.getConfigCenterName()}
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