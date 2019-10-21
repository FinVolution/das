/**
 * Created by liang.wang on 18/9/14.
 */
import React from 'react'
import ManagePanle from '../base/ManagePanle'
import TablePanle from '../base/TablePanle'
import {View} from 'ea-react-dm-v14'
import {DataBaseSyncControl} from '../../../../controller/Index'
import TreePanle from '../base/TreePanle'
import Immutable from 'immutable'
import {DataUtil} from '../../utils/util/Index'

@View(DataBaseSyncControl)
export default class DataBaseSyncManage extends ManagePanle {

    constructor(props, context) {
        super(props, context)
        this.modelName = 'DataBaseSyncModel'
        this.editorTitlte = '分组'
        this.searchInfo = this.modelName + '.searchInfo'
        this.objName = this.modelName + '.dbinfo'
        this.searchInfoSelectId = this.searchInfo + '.data.dal_group_id'
        this.loadList = this.props.loadList
        this.addItem = this.props.addGroupDb
        this.deleteItem = this.props.deleteGroupDb
        this.updateItem = this.props.updateGroupDb
        this.props.loadTree()
        this.initValueLink()
        this.state = {
            dbname: '',
            suggestInitData: null,
            suggestDisabled: false,
            title: '添加' + this.editorTitlte,
            confirmLoading: false,
            currentCheckedId: 0
        }
    }

    /** @Override **/
    addValidate = () => {
        if (this.getValueByReducers(this.objName + '.dal_group_id')) {
            return {
                state: true
            }
        } else {
            return {
                state: false,
                msg: '请选择组!!!'
            }
        }
    }

    /** @Override **/
    cleanObjName = () => {
        let item = this.getValueByReducers(this.objName).toJS()
        item.comment = ''
        item.id = ''
        this.setValueByReducers(this.objName, Immutable.fromJS(item))
    }

    /** @Override **/
    editorCallBack = item => {
        this.setState({dbname: item.dbname})
    }

    onSelect(selectedKeys, info) {
        const groupId = info.node.props.dataRef ? info.node.props.dataRef.id : null
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
    }

    selectedCallback = ele => {
        this.replaceValueToItem(this.objName, 'id', ele.id)
    }

    cancelCallback = () => {
        this.replaceValueToItem(this.objName, 'id', null)
    }

    sync = item => {
        item = DataUtil.ObjUtils.toNnull(item, ['id', 'update_time', 'create_time', 'insert_time', 'userRealName', 'update_user_no'])
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
        const {databasesyncmodel, setValueByReducers} = this.props
        return (<div>
            <TablePanle title='物理库同步' navigation='数据访问平台 / 物理库同步'
                        type={4}
                        lineTop={0}
                        sync={::this.sync}
                        addButtonShow={false}
                        modelName={this.modelName}
                        databasesyncmodel={databasesyncmodel}
                        setValueByReducers={setValueByReducers}
                        loadList={::this.loadList}
                        tree={<TreePanle
                            rootShow={false}
                            showLine={true}
                            format={{tree: {title: 'group_name', key: 'id', isLeaf: true}}}
                            treeUrl={window.DASENV.dasSyncTarget + '/group/tree?token=87679214010892'}
                            onSelect={::this.onSelect}
                            getDefaultSelected={::this.getDefaultSelected}/>}
            />
        </div>)
    }
}
