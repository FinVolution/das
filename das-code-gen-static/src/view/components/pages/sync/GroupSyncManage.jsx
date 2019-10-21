/**
 * Created by liang.wang on 18/9/14.
 */
import React from 'react'
import ManagePanle from '../base/ManagePanle'
import TablePanle from '../base/TablePanle'
import {View} from 'ea-react-dm-v14'
import {GroupSyncControl} from '../../../../controller/Index'
import {DataUtil} from '../../utils/util/Index'

@View(GroupSyncControl)
export default class GroupSyncManage extends ManagePanle {

    constructor(props, context) {
        super(props, context)
        this.modelName = 'GroupSyncModel'
        this.editorTitlte = '组'
        this.searchInfo = this.modelName + '.searchInfo'
        this.objName = this.modelName + '.group'
        this.syncLink = '/group/sync'
        this.loadList = this.props.loadList
        this.addItem = this.props.addGroup
        this.initValueLink()
        this.state = {}
    }

    sync = item => {
        item = DataUtil.ObjUtils.toNnull(item, ['id', 'update_time', 'children', 'icon', 'insert_time', 'update_user_no', 'userRealName', 'text'])
        this.addItem(item, this, (_this, rs) => {
            if (rs.code === 200) {
                this.reload()
                this.showSuccessMsg('同步成功')
            } else {
                this.showErrorsNotification(rs.msg)
            }
        })
    }

    render() {
        const states = this.getValueToJson(this.states)
        return (<div>
            {states.editeVisible ? ::this.createModel() : null}
            <TablePanle title='组同步' navigation='数据访问平台 / 组同步'
                        type={2}
                        lineTop={0}
                        sync={::this.sync}
                        addButtonShow={false}
                        modelName={this.modelName}
                        groupsyncmodel={this.props.groupsyncmodel}
                        setValueByReducers={this.props.setValueByReducers}
                        loadList={::this.loadList}/>
        </div>)
    }
}
