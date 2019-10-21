import {Model} from 'ea-react-dm-v14'
import {UserEnv} from '../../view/components/utils/util/Index'

@Model('ProjectModel')
export default class ProjectModel {
    static rs

    static list = []

    static suggestion = {}

    static searchInfo = {
        totalCount: 0,
        page: 1,
        pageSize: 10,
        sort: 'id',
        data: { //GenTaskByFreeSqlPojo
            dal_group_id: null
        }
    }

    static states = {
        editeVisible: false,
        checkVisible: false,
        addButtonShow: true
    }

    static columnInfo = {
        column: [
            {
                name: '项目名称',
                width: 14,
                key: 'name',
                sort: true,
                popover: {title: '项目名称', maximum: 25}
            },
            {
                name: 'APP ID',
                width: 6,
                key: 'app_id',
                sort: true
            },
            {
                name: '逻辑库',
                width: 15,
                key: 'dbsetNamees',
                popover: {title: '逻辑库', maximum: 30}
            },
            {
                name: '项目负责人',
                width: 6,
                key: 'projectUsers',
                search: false,
                popover: {title: '逻辑库', maximum: 15}
            },
            {
                name: '应用场景',
                width: 15,
                key: 'app_scene',
                popover: {title: '逻辑库', maximum: 30}
            },
            {
                name: '备注',
                width: 15,
                key: 'comment',
                popover: {title: '备注', maximum: 30}
            },
            {
                name: '新建时间',
                width: 7,
                key: 'insert_time',
                search: false,
                timePicker: {type: 'range'}
            },
            {
                name: '操作人',
                width: 5,
                key: 'userRealName',
                sort: true,
                sortKey: 'user_real_name',
                search: false
            },
            {
                name: '操作',
                width: 17,
                key: null,
                button: {
                    add: {display: true, key: 'add'},
                    editor: {display: true},
                    sync: {display: true, title: '若' + UserEnv.getConfigCenterName() + '操作失败，可使用此功能同步数据'},
                    check: {title: '查看' + UserEnv.getConfigCenterName() + '数据的正确性'},
                    delete: {display: true}
                }
            }
        ]
    }

    static tree = []

    static projectinfo = {
        name: '',
        namespace: 'com.ppdai.platform.das',
        dal_group_id: 0,
        app_scene: '',
        pre_release_time: '',
        app_id: '',
        dbsetIds: [],
        userIds: [],
        dbsetNamees: '',
        items: [],
        users: []
    }

    static member = {
        suggestionMember: {},
        user_id: null,
        group_id: null
    }
}