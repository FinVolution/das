import {Model} from 'ea-react-dm-v14'
import {UserEnv} from '../../view/components/utils/util/Index'

@Model('DatabaseModel')
export default class DatabaseModel {
    static rs

    static tree = []

    static list = []

    static searchInfo = {
        totalCount: 0,
        page: 1,
        pageSize: 10,
        sort: 'id',
        data: {}
    }

    static states = {
        editeVisible: false,
        checkVisible: false
    }

    static columnInfo = {
        column: [
            {
                name: '数据库标识符',
                width: 14,
                key: 'dbname',
                sort: true,
                sortKey: 'db_name'
            },
            {
                name: '类型',
                width: 4,
                map: {1: 'MySql', 2: 'SQLServer'},
                key: 'db_type',
                sort: true
            },
            {
                name: '所属组',
                width: 10,
                key: 'group_name',
                sort: true,
                sortKey: 'dal_group_id'
            },
            {
                name: '地址',
                width: 19,
                key: 'db_address'
            },
            {
                name: '端口',
                width: 3,
                key: 'db_port'
            },
            {
                name: '数据库名',
                width: 14,
                key: 'db_catalog'
            },
            {
                name: '备注',
                width: 8,
                key: 'comment',
                popover: {title: '查看备注', maximum: 20}
            },
            {
                name: '创建时间',
                width: 10,
                key: 'insert_time',
                sort: true,
                timePicker: {type: 'range'}
            },
            {
                name: '操作人',
                width: 4,
                key: 'userRealName',
                sort: true,
                sortKey: 'user_real_name',
                search: false
            },
            {
                name: '操作',
                width: 14,
                key: null,
                button: {
                    add: {display: true, key: 'add'},
                    editor: true, delete: true,
                    sync: {title: '若' + UserEnv.getConfigCenterName() + '作失败，可使用此功能同步数据'},
                    check: {title: '查看' + UserEnv.getConfigCenterName() + '数据的正确性'}
                }
            }
        ]
    }

    static item = {
        db_type: 1,
        db_address: '',
        db_port: '',
        db_user: '',
        db_password: ''
    }

    static db_catalogs = []

    static typelist = [
        {
            'id': 1,
            'name': 'string'
        },
        {
            'id': 2,
            'name': 'date'
        },
        {
            'id': 3,
            'name': 'int'
        }
    ]

    static dbconectInfo = {
        db_type: 1,
        db_address: '',
        db_port: '',
        db_user: '',
        db_password: ''
    }

    static dbconectSuggestion = {
        db_type: 1,
        db_address: '',
        db_port: '',
        db_user: '',
        db_password: ''
    }

    static dalGroupDBList = [
        /*{
            'id': 1,
            'dbRealName': 'roUserId',
            'dbname': 'roUserId01',
            'addToGroup': true,
            'dal_group_id': 1
        },
        {
            'id': 3,
            'dbRealName': 'userId',
            'dbname': 'roUser02',
            'addToGroup': true,
            'dal_group_id': 1
        },
        {
            'id': 4,
            'dbRealName': 'creationDate',
            'dbname': 'roUser93',
            'addToGroup': true,
            'dal_group_id': 1
        }*/
    ]

    static db_catalog_trees = []
}