import {Model} from 'ea-react-dm-v14'
import {UserEnv} from '../../view/components/utils/util/Index'

@Model('AppGroupModel')
export default class AppGroupModel {
    static rs

    static list = []

    static projectList = []

    static serverList = []

    static projects = []

    static searchInfo = {
        totalCount: 0,
        page: 1,
        pageSize: 10,
        sort: 'id'
    }

    static states = {
        editeVisible: false,
        checkVisible: false,
        editerType: 0 //0:add 1：update
    }

    static columnInfo = {
        column: [
            {
                name: 'ID',
                width: 5,
                key: 'id',
                sort: true,
                search: false
            },
            {
                name: '应用组名称',
                width: 10,
                key: 'name',
                sort: true
            },
            {
                name: '远程连接',
                width: 10,
                map: {0: '否', 1: '是'},
                key: 'serverEnabled'
            },
            {
                name: '服务器组名',
                width: 15,
                key: 'serverGroupName'
            },
            {
                name: '应用名',
                width: 10,
                key: 'projectNames',
                popover: {title: '应用名', maximum: 20}
            },
            {
                name: '备注',
                width: 10,
                key: 'comment',
                popover: {title: '查看备注', maximum: 20}
            },
            {
                name: '更新时间',
                width: 15,
                key: 'update_time',
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
                width: 20,
                key: null,
                button: {
                    editor: true, delete: true,
                    sync: {title: '若' + UserEnv.getConfigCenterName() + '操作失败，可使用此功能同步数据'},
                    check: {title: '查看' + UserEnv.getConfigCenterName() + '数据的正确性'}
                }
            }
        ]
    }

    static appGroup = {
        name: '',
        serverGroupId: 0,
        serverEnabled: 0,
        comment: '',
        insert_time: '',
        update_time: '',
        projectIds: [],
        items: []
    }

    static tree = []
}