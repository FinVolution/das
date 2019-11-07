import {Model} from 'ea-react-dm-v14'
import {UserEnv} from '../../view/components/utils/util/Index'

@Model('ProjectSyncModel')
export default class ProjectSyncModel {
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
                width: 17,
                key: 'name',
                sort: true,
                popover: {title: '逻辑库', maximum: 40}
            },
            {
                name: 'APP ID',
                width: 8,
                key: 'app_id',
                sort: true
            },
            {
                name: '逻辑库',
                width: 25,
                key: 'dbsetNamees',
                popover: {title: '逻辑库', maximum: 50}
            },
            {
                name: '应用场景',
                width: 18,
                key: 'app_scene',
                popover: {title: '逻辑库', maximum: 20}
            },
            {
                name: '备注',
                width: 14,
                key: 'comment',
                popover: {title: '备注', maximum: 20}
            },
            {
                name: '更新时间',
                width: 6,
                search: false,
                key: 'update_time'
            },
            {
                name: '操作',
                width: 8,
                key: null,
                button: {
                    sync: {title: '若' + UserEnv.getConfigCenterName() + '操作失败，可使用此功能同步数据'}
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
        release_time: '',
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