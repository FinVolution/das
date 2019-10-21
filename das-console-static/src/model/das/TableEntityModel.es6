import {Model} from 'ea-react-dm-v14'

@Model('TableEntityModel')
export default class TableEntityModel {

    static rs

    static tree = []

    static list = []

    static dbSetList = []

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
                key: 'view_names'
            },
            {
                name: '表名',
                width: 15,
                key: 'table_names'
            },
            {
                name: '自定义表名',
                width: 15,
                key: 'custom_table_name'
            },
            {
                name: '备注',
                width: 10,
                key: 'comment',
                popover: {title: '备注', maximum: 30}
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

    static task_table = {
        project_id: -1,
        dbset_id: null,
        db_name: '',
        comment: '',
        table_names: '',
        view_names: ''
    }

    static db_catalogs = []

    static tableEntryList = [
       /* {
            'custom_table_name': 'dal-1',
            'db_name': 'Master ',
            'comment': '21212',
            'table_names': '21212',
            'view_names': '21212',
            'dbset_id': 1,
            'project_id': 14
        }*/
    ]
}