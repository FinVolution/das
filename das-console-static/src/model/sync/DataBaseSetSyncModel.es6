import {Model} from 'ea-react-dm-v14'

@Model('DataBaseSetSyncModel')
export default class DataBaseSetSyncModel {
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
                width: 4,
                checkbox: true
            },
            {
                name: 'dbset 名称',
                width: 17,
                key: 'name',
                sort: true
            },
            {
                name: '策略类型',
                width: 8,
                map: {0: '无策略', 1: '私有策略', 2: '公共策略'},
                key: 'strategyType',
                sort: true,
                sortKey: 'strategy_type'
            },
            {
                name: '数据库类型',
                width: 8,
                map: {1: 'MySql', 2: 'SQLServer'},
                key: 'dbType',
                sort: true,
                sortKey: 'db_type'
            },
            {
                name: '策略',
                width: 29,
                key: 'strategySource',
                sort: true,
                popover: {title: '策略代码', placement: 'top', largest: 20, maximum: 55}
            },
            {
                name: '修改时间',
                width: 15,
                key: 'update_time',
                search: false,
                sort: true
            },
            {
                name: '操作人',
                width: 10,
                key: 'userRealName',
                search: false,
                sort: true,
                sortKey: 'user_real_name'
            },
            {
                name: '操作',
                width: 10,
                key: null,
                button: {sync: true, syncTitle: '同步数据到当前环境，可重复操作'}
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