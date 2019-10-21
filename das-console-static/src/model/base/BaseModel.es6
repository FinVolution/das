import {UserEnv} from '../../view/components/utils/util/Index'

export const dasConfig = {
    superid: 1
}

export const sysnc = {
    token: 'token=87679214010892'
}

export const column = {
    visible: true,
    sortArrow: 'both',
    classNametitle: 'text-align-center col-div-width col-color-hover noselect',
    classNameColumn: 'text-align-center col-div-width'
}

export const storageCode = {
    loadUserMenustorageCode: 'loadUserMenustorageCode',
    chartsdata: 'chartsdata',
    dataSource: 'dataSource'
}

export const das_msg = {
    apollo_namespace: '只能由小写英文字母,数字,下划线组成，' + UserEnv.getConfigCenterName() + 'NAMESPACE最长25个字符',
    ordinary_name: '只能由英文字母,数字,下划线组成',
    class_name: '只能由英文字母,数字,点组成',
    project_name: '只能由英文字母,数字,点,下划线组成'
}

export const dataFieldTypeEnum = {
    sql_date: 11,
    util_date: 12
}

export const display = {
    buttons_path: '.displayItems.buttons',
    custom: ['catalogs', 'saec', 'detail', 'download', 'simLogin'],
    buttons: {
        add: 'showAddButton',
        editor: 'showEditorButton',
        delete: 'showDeleteButton',
        download: 'showDownloadButton',
        sync: 'showSyncButton',
        check: 'showCkeckButton',
        saec: 'showSaecButton',
        detail: 'showDetailButton',
        catalogs: 'showCatalogsButton',
        simLogin: 'showSimLoginButton',
        checkAll:'showCkeckAllButton'
    }
}

export const databaseTypes = [{id: 1, name: 'mySql'}, {id: 2, name: 'sqlServer'}]

export const databaseShardingTypes = [{id: 1, name: 'Master'}, {id: 2, name: 'Slave'}]

export const roleTypes = [{id: 1, name: '管理员'}, {id: 2, name: '普通成员'}]

export const strategyType = [{id: 1, name: '静态加载的策略'}, {id: 2, name: '动态加载策略'}]

/*export const strategyDbsetType = [{id: 0, name: '无策略'}, {id: 1, name: '私有策略'}, {id: 2, name: '公共策略'}]*/

export const strategyDbsetType = [{id: 0, name: '无策略'}, {id: 1, name: '私有策略'}]

export const serverEnabled = [{id: 0, name: '否'}, {id: 1, name: '是'}]

export const fieldTypes = [{id: 11, name: 'java.sql.Timestamp'}, {id: 12, name: 'java.util.Date'}]

