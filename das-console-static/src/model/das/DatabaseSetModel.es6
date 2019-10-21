import {Model} from 'ea-react-dm-v14'
import {UserEnv} from '../../view/components/utils/util/Index'

@Model('DatabaseSetModel')
export default class DatabaseSetModel {
    static rs

    static list = []

    static suggestion = {}

    static dblist = []

    static searchInfo = {
        totalCount: 0,
        page: 1,
        pageSize: 10,
        sort: 'id',
        data: {//DatabaseSet
        }
    }

    static states = {
        editeVisible: false,
        checkVisible: false
    }

    static columnInfo = {
        column: [
            {
                name: '',
                width: 5,
                checkbox: true
            },
            {
                name: '逻辑库名',
                width: 15,
                key: 'name',
                sort: true
            },
            {
                name: '策略类型',
                width: 10,
                map: {0: '无策略', 1: '私有策略', 2: '公共策略'},
                key: 'strategyType',
                sort: true,
                sortKey: 'strategy_type'
            },
            {
                name: '数据库类型',
                width: 10,
                map: {1: 'MySql', 2: 'SQLServer'},
                key: 'dbType',
                sort: true,
                sortKey: 'db_type'
            },
            {
                name: 'sharding策略',
                width: 15,
                key: 'strategySource',
                popover: {title: '策略代码', placement: 'top', largest: 20}
            },
            {
                name: '修改时间',
                width: 20,
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
                width: 15,
                key: null,
                button: {
                    add: {display: true, key: 'add'},
                    checkAll: {display: true, key: 'checkAll'},
                    editor: {isAdmin: true},
                    delete: {isAdmin: true},
                    sync: {isAdmin: true, title: '若' + UserEnv.getConfigCenterName() + '操作失败，可使用此功能同步数据'},
                    check: {title: '查看' + UserEnv.getConfigCenterName() + '数据的正确性'}

                }
            }
        ]
    }

    static tree = []

    static dbSetInfo = {//DatabaseSet
        id: '',
        name: '    ',
        className: '',
        dbType: 1,
        strategySource: '',
        strategyType: 1,
        dynamicStrategyId: 0,
        groupId: '',
        apiParams: [
            /*{
                'key': 'key',
                'value': 'value'
            },
            {

                'key': 'key',
                'value': 'value'
            },
            {
                'key': '21212',
                'value': 'creationDate'
            }*/
        ]
    }

    static member = {
        suggestionMember: {},
        user_id: null,
        group_id: null
    }
}