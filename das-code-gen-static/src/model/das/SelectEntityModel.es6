import {Model} from 'ea-react-dm-v14'

@Model('SelectEntityModel')
export default class SelectEntityModel {

    static rs

    static tree = []

    static list = []

    static searchInfo = {
        totalCount: 0,
        page: 1,
        pageSize: 10,
        sort: 'id',
        data: { //GenTaskByFreeSqlPojo
            project_id: 0
        }
    }

    static states = {
        editeVisible: false,
        checkVisible: false
    }

    static columnInfo = {
        column: [
            {
                name: '逻辑数据库',
                width: 15,
                key: 'dbsetName'
            },
            {
                name: '类名',
                width: 15,
                key: 'class_name'
            },
            {
                name: '预览',
                width: 10,
                key: 'sql_content',
                popover: {title: 'SQL代码', type: 'sql', content: '查看'},
                search: false
            },
            {
                name: '备注',
                width: 15,
                key: 'comment',
                popover: {title: '备注', maximum: 30}
            },
            {
                name: '更新时间',
                width: 15,
                key: 'update_time',
                sort: true,
                search: false
            },
            {
                name: '操作人',
                width: 15,
                key: 'userRealName',
                sort: true,
                sortKey: 'user_real_name',
                search: false
            },
            {
                name: '操作',
                width: 10,
                key: null,
                button: {editor: true, delete: true}
            }
        ]
    }

    static task_sql = {
        project_id: -1,
        dbset_id: null,
        class_name: '',
        sql_content: '',
        comment: ''
    }
}