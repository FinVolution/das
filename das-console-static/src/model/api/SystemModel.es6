import {Model} from 'ea-react-dm-v14'

@Model('SystemModel')
export default class SystemModel {
    static list = []

    static actionState = ''

    static searchDbInfo = {
        data: {
            name: ''
        },
        page: 1,
        pageSize: 10
    }

    static apiConfig = {
        id: 0,
        systemName: '',
        apiPath: '',
        excuteSql: ''
    }

    static textArea = {
        value: ''
    }

    static dbListInfo = {
        column: [
            {
                name: '主键',
                width: 10,
                key: 'id'
            },
            {
                name: '系统项目名称',
                width: 35,
                key: 'name'
            },
            {
                name: '描述',
                width: 40,
                key: 'description'
            },
            {
                name: '操作',
                width: 15,
                key: null,
                button: null
            }
        ]
    }
}