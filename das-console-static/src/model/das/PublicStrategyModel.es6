import {Model} from 'ea-react-dm-v14'
import {UserEnv} from '../../view/components/utils/util/Index'

@Model('PublicStrategyModel')
export default class PublicStrategyModel {
    static rs

    static list = []

    static searchInfo = {
        totalCount: 0,
        page: 1,
        pageSize: 10,
        sort: 'id'
    }

    static states = {
        editeVisible: false,
        checkVisible: false
    }

    static columnInfo = {
        column: [
            {
                name: '策略名',
                width: 15,
                key: 'name',
                sort: true
            },
            {
                name: '类名(class name)',
                width: 20,
                key: 'className',
                popover: {title: '类名(class name)', maximum: 40},
                sort: true,
                sortKey: 'class_name'
            },
            {
                name: '策略类型',
                width: 10,
                map: {1: '静态加载', 2: '动态加载'},
                key: 'strategyLoadingType',
                sort: true,
                sortKey: 'strategy_loading_type'
            },
            {
                name: '策略代码',
                width: 10,
                key: 'strategySource',
                popover: {title: '策略代码', type: 'java', content: '查看'},
                search: false
            },
            {
                name: '更新时间',
                width: 15,
                key: 'updateTime',
                sort: true,
                sortKey: 'update_time',
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

    static publicStrategy = {
        name: '',
        className: '',
        strategyLoadingType: 1,
        strategySource: '',
        strategyParams: '',
        comment: '',
        apiParams: []
    }

    static tree = []
}