import {Model} from 'ea-react-dm-v14'

@Model('ServerConfigModel')
export default class ServerConfigModel {

    static rs

    static list = []

    static suggestion = {}

    static searchInfo = {
        totalCount: 0,
        page: 1,
        pageSize: 10,
        sort: 'id',
        data: {
            serverId:0
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
                name: 'Server Id',
                width: 10,
                key: 'serverId',
                sort: true
            },
            {
                name: 'key',
                width: 10,
                key: 'keya'
            },
            {
                name: 'value',
                width: 15,
                key: 'value'
            },
            {
                name: '备注',
                width: 25,
                key: 'comment'
            },
            {
                name: '创建时间',
                width: 15,
                key: 'create_time'
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

    static serverConfig = { //server
        serverId: '',
        keya: '',
        value: '',
        comment: ''
    }

    static member = {
        suggestionMember: {},
        user_id: null,
        group_id: null
    }
}