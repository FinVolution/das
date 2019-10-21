import {Model} from 'ea-react-dm-v14'

@Model('ProjectListModel')
export default class ProjectListModel {
    static rs

    static list = []

    static suggestion = {}

    static searchInfo = {
        totalCount: 0,
        page: 1,
        pageSize: 20,
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
                name: '序号',
                width: 3,
                type: 'sequence'
            },
            {
                name: '所属组',
                width: 10,
                key: 'groupName',
                sortKey: 'group_name',
                sort: true
            },
            {
                name: '项目名称',
                width: 13,
                key: 'name',
                sort: true,
                popover: {title: '逻辑库', maximum: 10}
            },
            {
                name: '项目中文名(等描述)',
                width: 22,
                key: 'comment',
                popover: {title: '备注', maximum: 10}
            },
            {
                name: 'APP ID',
                width: 8,
                key: 'app_id',
                sort: true,
                link: {url: 'api?appid='}
            },
            {
                name: '项目负责人',
                width: 8,
                key: 'projectUsers',
                popover: {title: '逻辑库', maximum: 15},
                search: false
            },
            {
                name: '应用场景',
                width: 15,
                key: 'app_scene',
                popover: {title: '逻辑库', maximum: 10}
            },
            {
                name: '新建时间',
                width: 7,
                key: 'insert_time',
                sort: true,
                timePicker: {type: 'range'}
            },
            {
                name: '预计上线时间',
                width: 7,
                key: 'pre_release_time',
                sort: true,
                timePicker: {type: 'range'}
            },
            {
                name: '首次上线时间',
                width: 7,
                key: 'first_release_time',
                sort: true,
                timePicker: {type: 'range'}
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