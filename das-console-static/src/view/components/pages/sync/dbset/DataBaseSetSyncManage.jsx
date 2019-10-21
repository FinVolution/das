/**
 * Created by liang.wang on 18/9/14.
 */
import React from 'react'
import ManagePanle from '../../base/ManagePanle'
import TablePanle from '../../base/TablePanle'
import {View} from 'ea-react-dm-v14'
import {DataBaseSetSyncControl} from '../../../../../controller/Index'
import TreePanle from '../../base/TreePanle'
import Immutable from 'immutable'
import DatabaseSetEntry from './DatabaseSetEntrySync'
import {DasUtil} from '../../../utils/util/Index'
import FrwkUtil from '../../../utils/util/FrwkUtil'
import DataUtil from '../../../utils/util/DataUtil'
import {sysnc} from '../../../../../model/base/BaseModel'

@View(DataBaseSetSyncControl)
export default class DataBaseSetSyncManage extends ManagePanle {

    constructor(props, context) {
        super(props, context)
        this.modelName = 'DataBaseSetSyncModel'
        this.editorTitlte = '逻辑数据库'
        this.searchInfo = this.modelName + '.searchInfo'
        this.objName = this.modelName + '.dbSetInfo'
        this.syncLink = '/groupdbset/sync'
        this.checkLink = '/groupdbset/check'
        this.searchInfoSelectId = this.searchInfo + '.data.groupId'
        this.loadList = this.props.loadList
        this.addItem = this.props.addDbSet
        this.props.loadTree()
        this.cleanExceptKeys = ['dbType', 'groupId']
        this.initValueLink()
        this.state = {
            title: '添加' + this.editorTitlte,
            groupId: 0,
            visible: false,
            checkVisible: false,
            suggestInitData: null,
            suggestDisabled: false,
            publicStrategyList: [],
            publicStrategyApiParams: [],
            confirmLoading: false,
            currentCheckedId: 0
        }
        this.loadPublicStrategy()
    }


    /** @Override **/
    loadListFilerBefore = data => {
        DasUtil.transformStrategyStrToList(data.list, 'strategySource')
        return data
    }

    onSelect(selectedKeys, info) {
        if (selectedKeys[0]) {
            const groupId = info.node.props.dataRef ? info.node.props.dataRef.id : null
            this.onSelectByGroupId(groupId)
        }
    }

    onSelectByGroupId = groupId => {
        let searchInfo = this.getValueByReducers(this.searchInfo).toJS()
        let objName = this.getValueByReducers(this.objName).toJS()
        searchInfo.data['groupId'] = groupId
        objName['groupId'] = groupId
        this.setState({groupId}, () => {
            this.loadList(searchInfo, this, (_this, rs) => {
                if (rs.code === 200) {
                    _this.setValueByReducers(_this.searchInfo, Immutable.fromJS(searchInfo))
                    _this.setValueByReducers(_this.objName, Immutable.fromJS(objName))
                } else {
                    _this.showErrorsNotification(rs.msg)
                }
            }, this.loadListFiler)
            this.props.loadDBList(groupId)
        })
    }


    loadPublicStrategy = () => {
        FrwkUtil.fetch.fetchGet('/publicStrategy/list', null, this, (data, _this) => {
                if (data.code === 200) {
                    DasUtil.transformStrategyStrToList(data.msg, 'strategyParams', 'placeholder')
                    _this.setState({publicStrategyList: data.msg})
                } else if (data.code === 500) {
                    _this.showErrorsMsg(data.msg)
                }
            }
        )
    }

    onChangeCallback = id => {
        if (id == 0) {//无策略
            //TODO
        }
        if (id == 1) {//私有
            //TODO
        }
        if (id == 2) {//共有
            //this.loadPublicStrategy()
        }
    }

    onChangeCallbBack = id => {
        if (id === 0) {
            return
        }
        const {publicStrategyList} = this.state
        const publicStrategy = DataUtil.ObjUtils.findWhere(publicStrategyList, {id: id})
        const publicStrategyApiParams = publicStrategy.apiParams
        this.setState({publicStrategyApiParams})
        this.replaceValuesToItem(this.objName, {
            className: publicStrategy.className,
            apiParams: publicStrategy.apiParams
        })
    }


    onChangeCheckbox = item => {
        item = item.toJS()
        const link = this.modelName + '.list'
        let items = this.getValueByReducers(link).toJS()
        items.list && items.list.forEach(e => {
            if (item.id == e.id) {
                e.checkbox = true
            } else {
                e.checkbox = false
            }
        })
        this.setState({currentCheckedId: item.id}, () => {
            this.setValueByReducers(link, items)
        })
    }

    getDefaultSelectedCallBack = id => {
        this.onSelectByGroupId(id)
    }

    sync = item => {
        item = DataUtil.ObjUtils.toNnull(item, ['id', 'update_time', 'app_group_id', 'create_time', 'insert_time', 'checkbox', 'updateUserNo'])
        this.addItem(item, this, (_this, rs) => {
            if (rs.code === 200) {
                this.showSuccessMsg('同步成功')
            } else {
                this.showErrorsNotification(rs.msg)
            }
        })
    }

    render() {
        const dblist = this.getValueToJson(this.modelName + '.dblist')
        const {databasesetsyncmodel, setValueByReducers} = this.props
        const dbSetInfo = this.getValueToJson(this.objName)
        const {currentCheckedId} = this.state
        return (<div>
            <TablePanle title='逻辑数据库同步' navigation='数据访问平台 / 逻辑数据库同步'
                        type={4}
                        lineTop={0}
                        modelName={this.modelName}
                        databasesetsyncmodel={databasesetsyncmodel}
                        onChangeCheckbox={::this.onChangeCheckbox}
                        sync={::this.sync}
                        addButtonShow={false}
                        setValueByReducers={setValueByReducers}
                        loadListFiler={::this.loadListFiler}
                        loadList={::this.loadList}
                        isloadList={false}
                        tree={<TreePanle rootShow={false}
                                         showLine={true}
                                         getDefaultSelected={::this.getDefaultSelected}
                                         treeUrl={window.DASENV.dasSyncTarget + '/group/tree?' + sysnc.token}
                                         format={{tree: {title: 'group_name', key: 'id', isLeaf: true}}}
                                         onSelect={::this.onSelect}/>}>
                <DatabaseSetEntry dbset_id={currentCheckedId} dblist={dblist} groupId={dbSetInfo.groupId}/>
            </TablePanle>
        </div>)
    }
}
