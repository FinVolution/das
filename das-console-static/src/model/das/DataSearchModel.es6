import {Model} from 'ea-react-dm-v14'

@Model('DataSearchModel')
export default class DataSearchModel {
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

    static dataSearch = {}

    static tree = []

    static columnInfo = {
        column: [
            {
                name: '操作人',
                width: 5,
                key: 'user_real_name',
                sort: true,
                sortKey: 'user_real_name'
            },
            {
                name: 'IP',
                width: 5,
                key: 'ip'
            },
            {
                name: '类型',
                width: 5,
                map: {1: '查询', 0: '下载'},
                key: 'request_type',
                sort: true
            },
            {
                name: '查询时间',
                width: 10,
                key: 'insert_time',
                sort: true,
                timePicker: {type: 'range'}
            },
            {
                name: 'success',
                width: 5,
                map: {true: '成功', false: '失败'},
                key: 'success'
            },
            {
                name: '请求参数',
                width: 30,
                key: 'request',
                sort: true
            },
            {
                name: '结果',
                width: 40,
                key: 'result'
            }
        ]
    }
}