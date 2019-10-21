import {Model} from 'ea-react-dm-v14'
import {UserEnv} from '../../view/components/utils/util/Index'

@Model('ServerModel')
export default class ServerModel {
    static rs

    static list = []

    static suggestion = {}

    static searchInfo = {
        totalCount: 0,
        page: 1,
        pageSize: 10,
        sort: 'id',
        data: {
            serverGroupId: 0
        }
    }

    static states = {
        editeVisible: false,
        checkVisible: false
    }

    static serverList = []

    static columnInfo = {
        column: [
            {
                name: '',
                width: 5,
                checkbox: true
            },
            {
                name: 'IP',
                width: 15,
                key: 'ip',
                sort: true
            },
            {
                name: 'Port',
                width: 5,
                key: 'port',
                sort: true
            },
            {
                name: '备注',
                width: 15,
                key: 'comment',
                popover: {title: '查看备注', maximum: 30}
            },
            {
                name: '修改时间',
                width: 15,
                key: 'update_time',
                sort: true
            },
            {
                name: '操作人',
                width: 15,
                key: 'userRealName'
            },
            {
                name: '操作',
                width: 15,
                key: null,
                button: {
                    editor: true, delete: true,
                    sync: {title: '若' + UserEnv.getConfigCenterName() + '操作失败，可使用此功能同步数据'},
                    check: {title: '查看' + UserEnv.getConfigCenterName() + '数据的正确性'}
                }
            }
        ]
    }

    static tree = []

    static server = { //server
        ip: '',
        serverGroupId: '',
        port: '',
        comment: ''
    }

    static member = {
        suggestionMember: {},
        user_id: null,
        group_id: null
    }
}