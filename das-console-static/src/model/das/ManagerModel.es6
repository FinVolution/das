import {Model} from 'ea-react-dm-v14'

@Model('ManagerModel')
export default class ManagerModel{
    static authList = {}

    //
    static instCapitalInfo = {
        column: [
            {
                name: '主键',
                width: 5,
                key: 'pkInstituteId'
            },
            {
                name: '机构编号',
                width: 6,
                key: 'instituteCode'
            },
            {
                name: '机构名称',
                width: 12,
                key: 'instituteName'
            },
            {
                name: '机构排序',
                width: 8,
                key: 'instituteSerial'
            },
            {
                name: '机构事件编号',
                width: 8,
                key: 'eventCode'
            },
            {
                name: '机构事件名称',
                width: 12,
                key: 'eventName'
            },
            {
                name: '版本号',
                width: 5,
                key: 'version'
            },
            {
                name: '登记人',
                width: 5,
                key: 'createUser'
            },
            {
                name: '更新人',
                width: 5,
                key: 'updateUser'
            },
            {
                name: '登记日期',
                width: 10,
                key: 'insertTime'
            },
            {
                name: '更新日期',
                width: 8,
                key: 'updateTime'
            },
            {
                name: '逻辑删除',
                width: 8,
                key: 'isActive'
            },
            {
                name: '操作',
                width: 8,
                key: null,
                button: {editor: true}
            }
        ]
    }
}