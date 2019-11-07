/**
 * Created by liang.wang on 18/9/14.
 */
import React from 'react'
import ManagePanle from '../base/ManagePanle'
import TablePanle from '../base/TablePanle'
import {View} from 'ea-react-dm-v14'
import {PojectSyncControl} from '../../../../controller/Index'
import TreePanle from '../base/TreePanle'
import Immutable from 'immutable'
import FrwkUtil from '../../utils/util/FrwkUtil'
import {DataUtil} from '../../utils/util/Index'
import {sysnc} from '../../../../model/base/BaseModel'

@View(PojectSyncControl)
export default class ProjecSynctManage extends ManagePanle {

    constructor(props, context) {
        super(props, context)
        this.modelName = 'ProjectSyncModel'
        this.editorTitlte = '项目'
        this.searchInfo = this.modelName + '.searchInfo'
        this.columnInfo = this.modelName + '.columnInfo'
        this.objName = this.modelName + '.projectinfo'
        this.searchInfoSelectId = this.searchInfo + '.data.dal_group_id'
        this.syncLink = '/project/sync'
        this.checkLink = '/project/check'
        this.addButtonBiden = [0]
        this.cleanExceptKeys = ['namespace', 'dal_group_id', {k: 'app_id', v: ''}]
        this.loadList = this.props.loadList
        this.addItem = this.props.addProject
        this.props.loadTree()
        this.initValueLink()
        this.state = {
            title: '添加' + this.editorTitlte,
            confirmLoading: false,
            currentCheckedId: 0,
            dbname: '',
            suggestInitData: null,
            suggestDisabled: false
        }
    }

    loadDbSetList = groupId => {
        FrwkUtil.load.getList('/groupdbset/' + groupId + '/list', null, this, 'dbSetlist')
    }

    loadGroupUsrs = groupId => {
        FrwkUtil.load.getList('/user/group/users', {groupId}, this, 'userlist')
    }

    bidenButton = () => {
        const columnInfo = this.getValueToJson(this.columnInfo)
        const column = columnInfo.column
        column[column.length - 1].visible = false
        this.setValueToImmutable(this.columnInfo, columnInfo)
    }

    showBoton = () => {
        const columnInfo = this.getValueToJson(this.columnInfo)
        const column = columnInfo.column
        column[column.length - 1].visible = true
        this.setValueToImmutable(this.columnInfo, columnInfo)
    }

    onSelectBefore = groupId => {
        groupId = parseInt(groupId)
        if (this.addButtonBiden.includes(groupId)) {
            this.bidenButton()
            this.setValueByReducers(this.states + '.addButtonShow', false)
        } else {
            this.showBoton()
            this.setValueByReducers(this.states + '.addButtonShow', true)
        }
        this.loadDbSetList(groupId)
        this.loadGroupUsrs(groupId)
    }

    onSelect = (selectedKeys, info) => {
        if (selectedKeys[0]) {
            const groupId = info.node.props.dataRef && info.node.props.dataRef ? info.node.props.dataRef.id : selectedKeys[0]
            this.onSelectBefore(groupId)
            let searchInfo = this.getValueByReducers(this.searchInfo).toJS()
            let objName = this.getValueByReducers(this.objName).toJS()
            searchInfo.data['dal_group_id'] = groupId
            objName['dal_group_id'] = groupId
            this.loadList(searchInfo, this, (_this, rs) => {
                if (rs.code === 200) {
                    _this.setValueByReducers(_this.searchInfo, Immutable.fromJS(searchInfo))
                    _this.setValueByReducers(_this.objName, Immutable.fromJS(objName))
                } else {
                    _this.showErrorsNotification(rs.msg)
                }
            })
            this.groupName = selectedKeys[0]
        }
    }

    selectedCallback = ele => {
        this.replaceValueToItem(this.objName, 'id', ele.id)
    }

    cancelCallback = () => {
        this.replaceValueToItem(this.objName, 'id', null)
    }

    getDefaultSelectedCallBack = groupId => {
        this.setValueByReducers(this.objName + '.dal_group_id', groupId)
        this.loadDbSetList(groupId)
        this.loadGroupUsrs(groupId)
    }

    sync = item => {
        item = DataUtil.ObjUtils.toNnull(item, ['id', 'update_time', 'app_group_id', 'create_time', 'insert_time', 'dal_config_name', 'projectUsers', 'userRealName', 'dbsetIds', 'userIds', 'update_user_no'])
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
        const {projectsyncmodel, setValueByReducers} = this.props
        return (<div>
            <TablePanle title='项目同步' navigation='数据访问平台 / 项目同步'
                        type={4}
                        lineTop={0}
                        sync={::this.sync}
                        addButtonShow={false}
                        modelName={this.modelName}
                        projectsyncmodel={projectsyncmodel}
                        setValueByReducers={setValueByReducers}
                        loadList={::this.loadList}
                        tree={<TreePanle showLine={true}
                                         treeUrl={window.DASENV.dasSyncTarget + '/group/tree?' + sysnc.token}
                                         format={{tree: {title: 'group_name', key: 'id', isLeaf: true}}}
                                         onSelect={::this.onSelect}
                                         getDefaultSelected={::this.getDefaultSelected}/>}
            />
        </div>)
    }
}
